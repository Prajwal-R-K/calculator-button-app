package com.procalc.dto;

public class EvaluateResponse {
    private String expression;
    private String result;
    private String formatted;
    private String status;
    private String message;

    public EvaluateResponse() {
    }

    public EvaluateResponse(String expression, String result, String formatted, String status) {
        this.expression = expression;
        this.result = result;
        this.formatted = formatted;
        this.status = status;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getFormatted() {
        return formatted;
    }

    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
