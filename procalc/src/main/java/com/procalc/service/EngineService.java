package com.procalc.service;

import com.procalc.dto.EvaluateResponse;
import com.procalc.engine.CalculatorEngine;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;

@Service
public class EngineService {
    private final CalculatorEngine engine;

    public EngineService() {
        // 28 digits precision, HALF_UP rounding
        this.engine = new CalculatorEngine(28, RoundingMode.HALF_UP);
    }

    public EvaluateResponse evaluate(String expression) {
        var res = engine.evaluateExpression(expression);
        EvaluateResponse r = new EvaluateResponse();
        r.setExpression(expression);
        if (!res.ok) {
            r.setStatus("ERROR");
            r.setMessage(res.message);
        } else {
            r.setStatus("OK");
            r.setResult(res.value.toPlainString());
            r.setFormatted(CalculatorEngine.format(res.value));
        }
        return r;
    }

    public EvaluateResponse preview(String expression) {
        // For now preview is same as evaluate
        return evaluate(expression);
    }
}
