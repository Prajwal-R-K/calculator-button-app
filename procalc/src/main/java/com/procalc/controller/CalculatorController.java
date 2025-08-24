package com.procalc.controller;

import com.procalc.dto.EvaluateRequest;
import com.procalc.dto.EvaluateResponse;
import com.procalc.service.EngineService;
import com.procalc.service.HistoryService;
import com.procalc.service.MemoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class CalculatorController {

    private final EngineService engineService;
    private final HistoryService historyService;
    private final MemoryService memoryService;

    public CalculatorController(EngineService engineService,
            HistoryService historyService,
            MemoryService memoryService) {
        this.engineService = engineService;
        this.historyService = historyService;
        this.memoryService = memoryService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<EvaluateResponse> evaluate(@RequestBody EvaluateRequest req) {
        EvaluateResponse resp = engineService.evaluate(req.getExpression());
        if ("OK".equalsIgnoreCase(resp.getStatus())) {
            historyService.add(resp.getExpression(), resp.getResult());
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/preview")
    public ResponseEntity<EvaluateResponse> preview(@RequestBody EvaluateRequest req) {
        EvaluateResponse resp = engineService.preview(req.getExpression());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/history")
    public ResponseEntity<?> history() {
        return ResponseEntity.ok(historyService.list());
    }

    @PostMapping("/history/clear")
    public ResponseEntity<?> clearHistory() {
        historyService.clear();
        return ResponseEntity.ok(Map.of("status", "OK"));
    }

    @GetMapping("/memory")
    public ResponseEntity<?> memory() {
        return ResponseEntity.ok(memoryService.recallAsMap());
    }

    @PostMapping("/memory")
    public ResponseEntity<?> memoryOp(@RequestBody Map<String, String> body) {
        memoryService.apply(body);
        return ResponseEntity.ok(memoryService.recallAsMap());
    }
}
