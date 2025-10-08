package com.rentops.ai.config;

import com.rentops.ai.client.GroqLlmClient;
import com.rentops.ai.client.LlmClient;
import com.rentops.ai.exceptions.AiException;
import com.rentops.ai.logging.AiQueryLogDao;
import com.rentops.ai.logging.LoggingLlmClient;
import com.rentops.ai.metrics.AiMetricsRegistry;
import com.rentops.ai.router.ModelRouter;
import com.rentops.ai.safety.NoopSafetyService;
import com.rentops.ai.safety.SafetyService;

/**
 * Central assembly of AI components honoring config & logging flag.
 */
public class AiComponents {

    public final AiConfig config;
    public final ModelRegistry registry;
    public final ModelRouter router;
    public final SafetyService safety;
    public final AiMetricsRegistry metrics;
    public final LlmClient llm;

    private AiComponents(AiConfig config, ModelRegistry registry, ModelRouter router, SafetyService safety, AiMetricsRegistry metrics, LlmClient llm) {
        this.config = config;
        this.registry = registry;
        this.router = router;
        this.safety = safety;
        this.metrics = metrics;
        this.llm = llm;
    }

    public static AiComponents build(boolean enableDbLogging) throws AiException {
        AiConfig cfg = AiConfig.load();
        ModelRegistry registry = new ModelRegistry();
        ModelRouter router = new ModelRouter(registry);
        SafetyService safety = new NoopSafetyService(); // later: real safety client
        AiMetricsRegistry metrics = new AiMetricsRegistry();
        LlmClient base = cfg.isEnabled() ? new GroqLlmClient(cfg) : request -> new LlmClient.LlmResponse("AI disabled", "none", 0);
        LlmClient decorated = base;
        if (cfg.isEnabled() && enableDbLogging) {
            decorated = new LoggingLlmClient(base, new AiQueryLogDao(), metrics, true);
        }
        return new AiComponents(cfg, registry, router, safety, metrics, decorated);
    }
}
