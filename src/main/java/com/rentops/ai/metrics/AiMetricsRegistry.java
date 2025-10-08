package com.rentops.ai.metrics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * In-memory metrics for AI calls; reset on app restart.
 */
public class AiMetricsRegistry {

    public static final class Key {

        public final String task;
        public final String model;

        public Key(String task, String model) {
            this.task = task;
            this.model = model;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            
            }if (!(o instanceof Key k)) {
                return false;
            
            }return task.equals(k.task) && model.equals(k.model);
        }

        @Override
        public int hashCode() {
            return task.hashCode() * 31 + model.hashCode();
        }
    }

    public static final class Snapshot {

        public final long calls;
        public final long errors;
        public final long totalLatencyMs;
        public final double avgLatencyMs;

        Snapshot(long calls, long errors, long totalLatencyMs) {
            this.calls = calls;
            this.errors = errors;
            this.totalLatencyMs = totalLatencyMs;
            this.avgLatencyMs = calls == 0 ? 0 : (double) totalLatencyMs / calls;
        }
    }

    private static final class Counters {

        LongAdder calls = new LongAdder();
        LongAdder errors = new LongAdder();
        LongAdder latency = new LongAdder();
    }

    private final Map<Key, Counters> data = new ConcurrentHashMap<>();

    public void record(String task, String model, long latencyMs, boolean success) {
        Key k = new Key(task, model);
        Counters c = data.computeIfAbsent(k, kk -> new Counters());
        c.calls.increment();
        if (!success) {
            c.errors.increment();
        }
        c.latency.add(latencyMs);
    }

    public Map<Key, Snapshot> snapshot() {
        Map<Key, Snapshot> snap = new ConcurrentHashMap<>();
        data.forEach((k, c) -> snap.put(k, new Snapshot(c.calls.sum(), c.errors.sum(), c.latency.sum())));
        return snap;
    }
}
