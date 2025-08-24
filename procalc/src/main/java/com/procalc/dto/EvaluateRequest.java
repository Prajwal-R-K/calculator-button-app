package com.procalc.dto;

public class EvaluateRequest {
    private String expression;

    public EvaluateRequest() {
    }

    public EvaluateRequest(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
