package com.procalc.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoryService {
    private final Deque<HistoryEntry> history = new ArrayDeque<>();
    private final int MAX = 50;

    public synchronized void add(String expr, String result) {
        if (expr == null)
            return;
        history.addLast(new HistoryEntry(expr, result, Instant.now()));
        if (history.size() > MAX)
            history.removeFirst();
    }

    public synchronized List<HistoryEntry> list() {
        return history.stream().collect(Collectors.toList());
    }

    public synchronized void clear() {
        history.clear();
    }

    public static class HistoryEntry {
        public final String expression;
        public final String result;
        public final Instant timestamp;

        public HistoryEntry(String expression, String result, Instant timestamp) {
            this.expression = expression;
            this.result = result;
            this.timestamp = timestamp;
        }

        public String getExpression() {
            return expression;
        }

        public String getResult() {
            return result;
        }

        public Instant getTimestamp() {
            return timestamp;
        }
    }
}
