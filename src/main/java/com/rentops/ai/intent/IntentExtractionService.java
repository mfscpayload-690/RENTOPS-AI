package com.rentops.ai.intent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentops.ai.client.LlmClient;
import com.rentops.ai.config.AiConfig;
import com.rentops.ai.exceptions.AiException;
import com.rentops.ai.router.AiTask;
import com.rentops.ai.router.ContextStats;
import com.rentops.ai.router.ModelRouter;
import com.rentops.ai.safety.SafetyService;
import com.rentops.ai.util.JsonCleaningUtil;
import com.rentops.ai.util.Redactor;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts booking-related intent from natural language queries. Strategy: 1.
 * Lightweight regex/heuristics for common fields. 2. If AI enabled and
 * heuristics incomplete, request structured JSON from LLM and merge.
 */
public class IntentExtractionService {

    private static final Pattern PASSENGERS = Pattern.compile("(for|with)\\s+([0-9]{1,2})\\s*(people|persons|passengers|pax)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern ACTION_CANCEL = Pattern.compile("cancel|call off", Pattern.CASE_INSENSITIVE);
    private static final Pattern ACTION_MODIFY = Pattern.compile("modify|change|update", Pattern.CASE_INSENSITIVE);
    private static final Pattern ACTION_QUERY = Pattern.compile("status|check|view", Pattern.CASE_INSENSITIVE);
    private static final Pattern VEHICLE = Pattern.compile("(suv|van|truck|electric|ev|luxury|economy|compact|midsize)", Pattern.CASE_INSENSITIVE);
    private static final Pattern LOCATION_SIMPLE = Pattern.compile("from\\s+([a-zA-Z]{2,}(?:[ -][a-zA-Z]{2,})*)\\s+(to|->)\\s+([a-zA-Z]{2,}(?:[ -][a-zA-Z]{2,})*)");
    private static final Pattern DATE_ISO = Pattern.compile("(20[2-9][0-9]-[01][0-9]-[0-3][0-9])");

    private final ObjectMapper mapper = new ObjectMapper();
    private final AiConfig config;
    private final ModelRouter router;
    private final LlmClient llmClient;
    private final SafetyService safetyService;

    public IntentExtractionService(AiConfig config, ModelRouter router, LlmClient llmClient, SafetyService safetyService) {
        this.config = config;
        this.router = router;
        this.llmClient = llmClient;
        this.safetyService = safetyService;
    }

    public BookingIntent extract(String raw) throws AiException {
        if (raw == null || raw.isBlank()) {
            return BookingIntent.empty(raw);
        }
        String lowered = raw.toLowerCase(Locale.ROOT);
        BookingAction action = inferAction(lowered);
        Integer passengers = inferPassengers(lowered);
        String vehicle = inferVehicle(lowered);
        LocalDate today = LocalDate.now();
        LocalDate pickup = inferFirstDate(lowered, today).orElse(null);
        LocalDate dropoff = inferSecondDate(lowered, today, pickup).orElse(null);
        String[] locs = inferLocations(raw);
        BookingIntent heuristic = new BookingIntent(action, locs[0], locs[1], pickup, dropoff, passengers, vehicle, raw, false);

        if (!config.isEnabled()) {
            return heuristic; // AI disabled path
        }

        boolean incomplete = heuristic.pickupDate() == null || heuristic.dropoffDate() == null || heuristic.pickupLocation() == null;
        if (!incomplete) {
            return heuristic; // good enough
        }

        // Safety first (redact and allow)
        String redacted = Redactor.redact(raw);
        var safety = safetyService.classify(redacted);
        if (!safety.allowed()) {
            return heuristic; // do not escalate if flagged
        }

        String model = router.pickModel(AiTask.INTENT, new ContextStats(raw.length(), 1, 0, false), 0, false);
        String prompt = buildJsonPrompt(redacted);
        try {
            var request = new LlmClient.LlmRequest(model, "You are an intent extraction assistant.", prompt, 0.2);
            var response = llmClient.chat(request);
            BookingIntent fromLlm = parseLlmIntent(response.rawText(), raw, heuristic);
            return merge(heuristic, fromLlm);
        } catch (AiException e) {
            // fall back to heuristic only
            return heuristic;
        }
    }

    private BookingIntent merge(BookingIntent base, BookingIntent llm) {
        if (llm == null) {
            return base;
        }
        return new BookingIntent(
                base.action() == BookingAction.UNKNOWN ? llm.action() : base.action(),
                base.pickupLocation() == null ? llm.pickupLocation() : base.pickupLocation(),
                base.dropoffLocation() == null ? llm.dropoffLocation() : base.dropoffLocation(),
                base.pickupDate() == null ? llm.pickupDate() : base.pickupDate(),
                base.dropoffDate() == null ? llm.dropoffDate() : base.dropoffDate(),
                base.passengers() == null ? llm.passengers() : base.passengers(),
                base.vehicleType() == null ? llm.vehicleType() : base.vehicleType(),
                base.rawText(),
                llm.usedLlm() || base.usedLlm()
        );
    }

    private BookingIntent parseLlmIntent(String response, String raw, BookingIntent heuristic) {
        String cleaned = JsonCleaningUtil.stripCodeFences(response);
        String json = JsonCleaningUtil.extractFirstJsonObject(cleaned);
        if (json == null) {
            return null;
        }
        JsonNode node;
        try {
            node = mapper.readTree(json);
        } catch (Exception ex) {
            return null;
        }
        BookingAction action = heuristic.action();
        if (node.hasNonNull("action")) {
            try {
                action = BookingAction.valueOf(node.get("action").asText().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
            }
        }
        String pickup = pick(node, "pickupLocation", heuristic.pickupLocation());
        String drop = pick(node, "dropoffLocation", heuristic.dropoffLocation());
        LocalDate pDate = date(node, "pickupDate", heuristic.pickupDate());
        LocalDate dDate = date(node, "dropoffDate", heuristic.dropoffDate());
        Integer pax = integer(node, "passengers", heuristic.passengers());
        String vehicle = pick(node, "vehicleType", heuristic.vehicleType());
        return new BookingIntent(action, pickup, drop, pDate, dDate, pax, vehicle, raw, true);
    }

    private String pick(JsonNode n, String field, String fallback) {
        return n.hasNonNull(field) ? n.get(field).asText() : fallback;
    }

    private LocalDate date(JsonNode n, String field, LocalDate fallback) {
        if (n.hasNonNull(field)) {
            try {
                return LocalDate.parse(n.get(field).asText());
            } catch (Exception ignored) {
            }
        }
        return fallback;
    }

    private Integer integer(JsonNode n, String field, Integer fallback) {
        if (n.hasNonNull(field) && n.get(field).canConvertToInt()) {
            return n.get(field).asInt();
        }
        return fallback;
    }

    private String buildJsonPrompt(String redacted) {
        return "Extract booking intent as strict JSON with fields: action(one of CREATE, MODIFY, CANCEL, QUERY), pickupLocation, dropoffLocation, pickupDate(YYYY-MM-DD), dropoffDate(YYYY-MM-DD), passengers(integer), vehicleType. If unknown use null. Input: " + redacted + "\nJSON:";
    }

    private BookingAction inferAction(String text) {
        if (ACTION_CANCEL.matcher(text).find()) {
            return BookingAction.CANCEL;
        }
        if (ACTION_MODIFY.matcher(text).find()) {
            return BookingAction.MODIFY;
        }
        if (ACTION_QUERY.matcher(text).find()) {
            return BookingAction.QUERY;
        }
        // Default assumption: create
        return BookingAction.CREATE;
    }

    private Integer inferPassengers(String text) {
        Matcher m = PASSENGERS.matcher(text);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(2));
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private String inferVehicle(String text) {
        Matcher m = VEHICLE.matcher(text);
        if (m.find()) {
            String v = m.group(1).toLowerCase(Locale.ROOT);
            return switch (v) {
                case "ev", "electric" ->
                    "ELECTRIC";
                default ->
                    v.toUpperCase(Locale.ROOT);
            };
        }
        return null;
    }

    private String[] inferLocations(String raw) {
        Matcher m = LOCATION_SIMPLE.matcher(raw);
        if (m.find()) {
            return new String[]{m.group(1), m.group(3)};
        }
        return new String[]{null, null};
    }

    private Optional<LocalDate> inferFirstDate(String text, LocalDate today) {
        Matcher iso = DATE_ISO.matcher(text);
        if (iso.find()) {
            return Optional.of(LocalDate.parse(iso.group(1)));
        }
        return RelativeDateResolver.resolveSingle(text, today);
    }

    private Optional<LocalDate> inferSecondDate(String text, LocalDate today, LocalDate first) {
        // naive: look for a second ISO date after the first
        Matcher iso = DATE_ISO.matcher(text);
        int count = 0;
        LocalDate second = null;
        while (iso.find()) {
            LocalDate d = LocalDate.parse(iso.group(1));
            if (count == 1) {
                second = d;
                break;
            }
            count++;
        }
        if (second != null) {
            return Optional.of(second);
        }
        // if we have a weekend expression maybe set a range
        var range = RelativeDateResolver.resolveRange(text, today);
        if (range.isPresent()) {
            var r = range.get();
            if (first == null) {
                return Optional.of(r.start());
            }
            return Optional.of(r.end());
        }
        return Optional.empty();
    }
}
