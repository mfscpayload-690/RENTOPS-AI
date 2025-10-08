package com.rentops.ai.logging;

import com.rentops.ai.client.LlmClient;
import com.rentops.ai.exceptions.AiException;
import com.rentops.ai.metrics.AiMetricsRegistry;
import com.rentops.ai.router.AiTask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoggingLlmClientTest {

    static class StubClient implements LlmClient {

        @Override
        public LlmResponse chat(LlmRequest request) {
            return new LlmResponse("hello", request.model(), 5);
        }
    }

    static class NoopDao extends AiQueryLogDao {

        @Override
        public void insert(AiQueryLog log) {
            /* no-op for test */ }
    }

    @Test
    void metricsIncrementOnSuccess() throws AiException {
        AiMetricsRegistry metrics = new AiMetricsRegistry();
        LoggingLlmClient client = new LoggingLlmClient(new StubClient(), new NoopDao(), metrics, true);
        var resp = client.chatWithTask(AiTask.INTENT, new LlmClient.LlmRequest("test-model", "sys", "user", 0.2));
        assertEquals("hello", resp.rawText());
        var snap = metrics.snapshot();
        assertEquals(1, snap.values().iterator().next().calls);
        assertEquals(0, snap.values().iterator().next().errors);
    }
}
