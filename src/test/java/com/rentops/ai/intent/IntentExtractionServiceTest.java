package com.rentops.ai.intent;

import com.rentops.ai.client.LlmClient;
import com.rentops.ai.config.AiConfig;
import com.rentops.ai.config.ModelRegistry;
import com.rentops.ai.router.ModelRouter;
import com.rentops.ai.safety.NoopSafetyService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntentExtractionServiceTest {

    private IntentExtractionService serviceWithHeuristicsOnly() {
        // Force disabled by ensuring no key present (AiConfig.load will pick that up)
        AiConfig cfg = AiConfig.load();
        ModelRouter router = new ModelRouter(new ModelRegistry());
        // Safe dummy: returns empty JSON so merge does nothing if accidentally invoked.
        LlmClient dummy = new LlmClient() {
            @Override
            public LlmResponse chat(LlmRequest request) {
                return new LlmResponse("{}", request.model(), 0);
            }
        };
        return new IntentExtractionService(cfg, router, dummy, new NoopSafetyService());
    }

    @Test
    void testPassengersExtraction() throws Exception {
        var svc = serviceWithHeuristicsOnly();
        var intent = svc.extract("Book an SUV for 5 people next Monday to next Wednesday from Boston to New York");
        assertEquals(5, intent.passengers());
        assertEquals("SUV", intent.vehicleType());
        assertEquals(BookingAction.CREATE, intent.action());
    }

    @Test
    void testCancelAction() throws Exception {
        var svc = serviceWithHeuristicsOnly();
        var intent = svc.extract("Please cancel my reservation");
        assertEquals(BookingAction.CANCEL, intent.action());
    }

    @Test
    void testModifyAction() throws Exception {
        var svc = serviceWithHeuristicsOnly();
        var intent = svc.extract("Can I modify pickup date to 2025-12-01?");
        assertEquals(BookingAction.MODIFY, intent.action());
    }

    @Test
    void testVehicleNormalizationEv() throws Exception {
        var svc = serviceWithHeuristicsOnly();
        var intent = svc.extract("Need an EV for 2 passengers");
        assertEquals("ELECTRIC", intent.vehicleType());
    }
}
