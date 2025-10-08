package com.rentops.ai.safety;

import com.rentops.ai.client.LlmClient;
import com.rentops.ai.client.LlmClient.LlmRequest;
import com.rentops.ai.config.ModelRegistry;
import com.rentops.ai.exceptions.AiException;
import com.rentops.ai.router.AiTask;
import com.rentops.ai.router.ContextStats;
import com.rentops.ai.router.ModelRouter;

/**
 * Basic implementation calling guard model; currently returns allow=true stub
 * until prompt added.
 */
public class SafetyServiceImpl implements SafetyService {

    private final LlmClient client;
    private final ModelRouter router;
    private final ModelRegistry registry;

    public SafetyServiceImpl(LlmClient client, ModelRouter router, ModelRegistry registry) {
        this.client = client;
        this.router = router;
        this.registry = registry;
    }

    @Override
    public SafetyResult classify(String userText) throws AiException {
        String model = router.pickModel(AiTask.SAFETY_CLASSIFY, ContextStats.basic(userText.length()), 0, false);
        // Placeholder: just echoes allow true. Later: real JSON extraction & parsing.
        LlmClient.LlmResponse resp = client.chat(new LlmRequest(model, "Classify content", userText, 0.0));
        return new SafetyResult(true, "stub");
    }
}
