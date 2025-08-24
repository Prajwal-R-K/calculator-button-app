package com.procalc.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class MemoryService {
    private BigDecimal mem = BigDecimal.ZERO;

    public synchronized void add(BigDecimal v) {
        mem = mem.add(v);
    }

    public synchronized void subtract(BigDecimal v) {
        mem = mem.subtract(v);
    }

    public synchronized void clear() {
        mem = BigDecimal.ZERO;
    }

    public synchronized BigDecimal recall() {
        return mem;
    }

    /**
     * Apply memory operation: expects keys "op" and "value"
     * op in {MC, MR, M+, M-}
     */
    public synchronized void apply(java.util.Map<String, String> body) {
        String op = body.getOrDefault("op", "").toUpperCase();
        String valStr = body.getOrDefault("value", "0");
        BigDecimal val;
        try {
            val = new BigDecimal(valStr);
        } catch (Exception e) {
            val = BigDecimal.ZERO;
        }
        switch (op) {
            case "MC" -> clear();
            case "M+" -> add(val);
            case "M-" -> subtract(val);
            case "MR" -> {
                /* no-op */ }
            default -> {
                /* ignore unknown */ }
        }
    }

    public Map<String, String> recallAsMap() {
        return Map.of("memory", mem.toPlainString());
    }
}
