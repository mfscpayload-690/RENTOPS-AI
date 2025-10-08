package com.rentops.ai.logging;

import com.rentops.ai.client.LlmClient;
import com.rentops.ai.exceptions.AiException;
import com.rentops.ai.metrics.AiMetricsRegistry;
import com.rentops.ai.router.AiTask;
import com.rentops.ai.util.HashUtil;

/**
 * Decorator adding DB logging + metrics.
 */
public class LoggingLlmClient implements LlmClient {

    private final LlmClient delegate;
    private final AiQueryLogDao dao;
    private final AiMetricsRegistry metrics;
    private final boolean persist;

    public LoggingLlmClient(LlmClient delegate, AiQueryLogDao dao, AiMetricsRegistry metrics, boolean persist) {
        this.delegate = delegate;
        this.dao = dao;
        this.metrics = metrics;
        this.persist = persist;
    }

    @Override
    public LlmResponse chat(LlmRequest request) throws AiException {
        return chatWithTask(AiTask.INTENT, request); // default fallback
    }

    public LlmResponse chatWithTask(AiTask task, LlmRequest request) throws AiException {
        long start = System.currentTimeMillis();
        boolean success = false;
        String errorType = null;
        String responseText = null;
        try {
            LlmResponse resp = delegate.chat(request);
            responseText = resp.rawText();
            success = true;
            return resp;
        } catch (AiException e) {
            errorType = e.getClass().getSimpleName();
            throw e;
        } finally {
            long latency = System.currentTimeMillis() - start;
            AiTask useTask = task == null ? AiTask.INTENT : task;
            metrics.record(useTask.name(), request.model(), latency, success);
            if (persist) {
                try {
                    String promptCombined = (request.systemPrompt() == null ? "" : request.systemPrompt()) + "\n" + request.userPrompt();
                    String hash = HashUtil.sha256(promptCombined);
                    int promptChars = promptCombined.length();
                    int responseChars = responseText == null ? 0 : responseText.length();
                    AiQueryLog log = AiQueryLog.now(useTask.name(), request.model(), hash, promptChars, responseChars, latency, success, errorType);
                    dao.insert(log);
                } catch (Exception ignored) {
                    // Swallow logging persistence errors
                }
            }
        }
    }
}
