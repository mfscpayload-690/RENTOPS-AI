package com.rentops.ai.intent;

import com.rentops.ai.client.LlmClient;
import com.rentops.ai.config.AiConfig;
import com.rentops.ai.config.ModelRegistry;
import com.rentops.ai.router.ModelRouter;
import com.rentops.ai.safety.NoopSafetyService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Minimal heuristic test to validate passenger & vehicle extraction path
 * without invoking AI.
 */
public class HeuristicParsingTest {

    @Test
    void basicPassengerAndVehicle() throws Exception {
        AiConfig cfg = AiConfig.load(); // disabled if key absent
        ModelRouter router = new ModelRouter(new ModelRegistry());
        LlmClient dummy = request -> new LlmClient.LlmResponse("{}", request.model(), 0); // should not be used
        var intentService = new IntentExtractionService(cfg, router, dummy, new NoopSafetyService());
        var intent = intentService.extract("Need an SUV for 4 people tomorrow");
        assertEquals(4, intent.passengers());
        assertEquals("SUV", intent.vehicleType());
        assertEquals(BookingAction.CREATE, intent.action());
    }
}
