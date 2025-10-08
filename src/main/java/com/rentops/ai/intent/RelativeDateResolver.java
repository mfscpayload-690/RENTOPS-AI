package com.rentops.ai.intent;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves relative or natural language date expressions into concrete dates.
 * Focused on lightweight heuristics to avoid LLM calls for common phrases.
 */
public class RelativeDateResolver {

    private static final Pattern IN_X_DAYS = Pattern.compile("in\\s+([0-9]{1,2})\\s+days?", Pattern.CASE_INSENSITIVE);
    private static final Pattern NEXT_DAY_OF_WEEK = Pattern.compile("next\\s+(monday|tuesday|wednesday|thursday|friday|saturday|sunday)", Pattern.CASE_INSENSITIVE);
    private static final Pattern THIS_WEEKEND = Pattern.compile("(this|the)\\s+weekend", Pattern.CASE_INSENSITIVE);
    private static final Pattern EXPLICIT_DATE = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})"); // ISO quick path

    public record DateRange(LocalDate start, LocalDate end) {

    }

    public static Optional<LocalDate> resolveSingle(String text, LocalDate today) {
        text = text.trim().toLowerCase(Locale.ROOT);
        switch (text) {
            case "today" -> {
                return Optional.of(today);
            }
            case "tomorrow" -> {
                return Optional.of(today.plusDays(1));
            }
        }
        // ISO date
        Matcher iso = EXPLICIT_DATE.matcher(text);
        if (iso.find()) {
            try {
                return Optional.of(LocalDate.parse(iso.group(1)));
            } catch (DateTimeParseException ignored) {
            }
        }
        Matcher inDays = IN_X_DAYS.matcher(text);
        if (inDays.find()) {
            int d = Integer.parseInt(inDays.group(1));
            return Optional.of(today.plusDays(d));
        }
        Matcher nextDow = NEXT_DAY_OF_WEEK.matcher(text);
        if (nextDow.find()) {
            DayOfWeek target = parseDow(nextDow.group(1));
            return Optional.of(next(target, today));
        }
        return Optional.empty();
    }

    public static Optional<DateRange> resolveRange(String text, LocalDate today) {
        Matcher weekend = THIS_WEEKEND.matcher(text);
        if (weekend.find()) {
            LocalDate nextSat = next(DayOfWeek.SATURDAY, today.minusDays(1));
            LocalDate nextSun = next(DayOfWeek.SUNDAY, nextSat.minusDays(1));
            return Optional.of(new DateRange(nextSat, nextSun));
        }
        return Optional.empty();
    }

    private static LocalDate next(DayOfWeek target, LocalDate from) {
        LocalDate d = from;
        do {
            d = d.plusDays(1);
        } while (d.getDayOfWeek() != target);
        return d;
    }

    private static DayOfWeek parseDow(String s) {
        return DayOfWeek.valueOf(s.toUpperCase(Locale.ROOT));
    }
}
