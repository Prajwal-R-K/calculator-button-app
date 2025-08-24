package com.procalc.engine;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Simple calculator engine: tokenize -> shunting-yard -> RPN evaluate using
 * BigDecimal.
 * Supports + - * / ^, functions (sin, cos, tan, sqrt, log, ln, abs, pow),
 * constants (pi, e), postfix percent % and factorial !.
 */
public class CalculatorEngine {
    private final MathContext mathContext;

    public CalculatorEngine(int precision, RoundingMode mode) {
        this.mathContext = new MathContext(precision, mode);
    }

    public static class EvalResult {
        public final boolean ok;
        public final BigDecimal value;
        public final String message;

        public EvalResult(boolean ok, BigDecimal value, String message) {
            this.ok = ok;
            this.value = value;
            this.message = message;
        }
    }

    /**
     * Tokenize input string into simple tokens (numbers, operators, parentheses,
     * percent, functions, commas for function args).
     * Note: this is a pragmatic tokenizer suitable for calculator expressions.
     */
    public List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        if (expr == null)
            return tokens;
        String s = expr.trim();
        StringBuilder num = new StringBuilder();
        StringBuilder ident = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                num.append(c);
            } else if (Character.isAlphabetic(c)) {
                if (num.length() > 0) { tokens.add(num.toString()); num.setLength(0); }
                ident.append(c);
            } else if (c == ',') {
                if (num.length() > 0) { tokens.add(num.toString()); num.setLength(0); }
                if (ident.length() > 0) { tokens.add(ident.toString()); ident.setLength(0); }
                tokens.add(",");
            } else if (Character.isWhitespace(c)) {
                if (num.length() > 0) {
                    tokens.add(num.toString());
                    num.setLength(0);
                }
                if (ident.length() > 0) {
                    tokens.add(ident.toString());
                    ident.setLength(0);
                }
            } else {
                if (num.length() > 0) {
                    tokens.add(num.toString());
                    num.setLength(0);
                }
                if (ident.length() > 0) {
                    tokens.add(ident.toString());
                    ident.setLength(0);
                }
                if ("+-*/^()".indexOf(c) >= 0)
                    tokens.add(String.valueOf(c));
                else if (c == '×')
                    tokens.add("*");
                else if (c == '÷')
                    tokens.add("/");
                else if (c == '%')
                    tokens.add("%");
                else if (c == '!')
                    tokens.add("!");
                // ignore other characters
            }
        }
        if (num.length() > 0)
            tokens.add(num.toString());
        if (ident.length() > 0)
            tokens.add(ident.toString());

        // Handle unary minus by inserting 0 before unary '-'
        List<String> norm = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            String t = tokens.get(i);
            if ("-".equals(t)) {
                if (i == 0 || "+-*/(".contains(tokens.get(i - 1))) {
                    norm.add("0");
                    norm.add("-");
                    continue;
                }
            }
            norm.add(t);
        }
        return norm;
    }

    private boolean isNumericToken(String t) {
        return t != null && t.matches("-?\\d+(?:\\.\\d+)?");
    }

    private boolean isOperator(String t) { return "+-*/^".contains(t); }

    private boolean isFunction(String t) {
        return "sin".equalsIgnoreCase(t) || "cos".equalsIgnoreCase(t) || "tan".equalsIgnoreCase(t)
                || "sqrt".equalsIgnoreCase(t) || "log".equalsIgnoreCase(t) || "ln".equalsIgnoreCase(t)
                || "abs".equalsIgnoreCase(t) || "pow".equalsIgnoreCase(t);
    }

    private boolean isConstant(String t) {
        return t != null && ("pi".equalsIgnoreCase(t) || "e".equalsIgnoreCase(t));
    }

    private BigDecimal constantValue(String t) {
        if ("pi".equalsIgnoreCase(t)) return new BigDecimal(Math.PI, mathContext);
        if ("e".equalsIgnoreCase(t)) return new BigDecimal(Math.E, mathContext);
        return BigDecimal.ZERO;
    }

    private int precedence(String op) {
        return switch (op) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "^" -> 3; // right-associative
            default -> 0;
        };
    }

    private boolean isRightAssociative(String op) { return "^".equals(op); }

    /**
     * Convert infix tokens to RPN using shunting-yard algorithm.
     */
    public List<String> toRPN(List<String> tokens) {
        List<String> out = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();
        for (String t : tokens) {
            if (t == null || t.isEmpty())
                continue;
            if (isNumericToken(t)) {
                out.add(t);
            } else if (isConstant(t)) {
                out.add(constantValue(t).toPlainString());
            } else if (isFunction(t)) {
                stack.push(t.toLowerCase());
            } else if (isOperator(t)) {
                while (!stack.isEmpty() && (isOperator(stack.peek()) || isFunction(stack.peek())) &&
                        (precedence(stack.peek()) > precedence(t) ||
                         (precedence(stack.peek()) == precedence(t) && !isRightAssociative(t)))) {
                    out.add(stack.pop());
                }
                stack.push(t);
            } else if ("(".equals(t)) {
                stack.push(t);
            } else if (")".equals(t)) {
                while (!stack.isEmpty() && !"(".equals(stack.peek())) out.add(stack.pop());
                if (stack.isEmpty() || !"(".equals(stack.peek()))
                    throw new IllegalArgumentException("Mismatched parentheses");
                stack.pop();
                if (!stack.isEmpty() && isFunction(stack.peek())) {
                    out.add(stack.pop());
                }
            } else if (",".equals(t)) {
                // function arg separator: pop until '('
                while (!stack.isEmpty() && !"(".equals(stack.peek())) out.add(stack.pop());
                if (stack.isEmpty()) throw new IllegalArgumentException("Misplaced comma");
            } else if ("%".equals(t)) {
                // percent as postfix: push as token to output (to be handled in eval)
                out.add("%");
            } else if ("!".equals(t)) {
                // factorial as postfix
                out.add("!");
            } else {
                throw new IllegalArgumentException("Unknown token: " + t);
            }
        }
        while (!stack.isEmpty()) {
            String s = stack.pop();
            if ("()".contains(s))
                throw new IllegalArgumentException("Mismatched parentheses");
            out.add(s);
        }
        return out;
    }

    /**
     * Evaluate RPN list and return result or error.
     */
    public EvalResult evalRPN(List<String> rpn) {
        Deque<BigDecimal> st = new ArrayDeque<>();
        try {
            for (String t : rpn) {
                if (isNumericToken(t)) {
                    st.push(new BigDecimal(t, mathContext));
                } else if ("%".equals(t)) {
                    if (st.isEmpty())
                        return new EvalResult(false, null, "Percent used incorrectly");
                    BigDecimal a = st.pop();
                    BigDecimal res = a.divide(BigDecimal.valueOf(100), mathContext);
                    st.push(res);
                } else if (isOperator(t)) {
                    if (st.size() < 2)
                        return new EvalResult(false, null, "Malformed expression");
                    BigDecimal b = st.pop();
                    BigDecimal a = st.pop();
                    BigDecimal r;
                    switch (t) {
                        case "+" -> r = a.add(b, mathContext);
                        case "-" -> r = a.subtract(b, mathContext);
                        case "*" -> r = a.multiply(b, mathContext);
                        case "/" -> {
                            if (b.compareTo(BigDecimal.ZERO) == 0)
                                return new EvalResult(false, null, "Division by zero");
                            r = a.divide(b, mathContext);
                        }
                        case "^" -> {
                            double rd = Math.pow(a.doubleValue(), b.doubleValue());
                            r = new BigDecimal(rd, mathContext);
                        }
                        default -> throw new IllegalStateException("Unknown operator " + t);
                    }
                    st.push(r);
                } else if (isFunction(t)) {
                    switch (t) {
                        case "sin" -> {
                            if (st.isEmpty()) return new EvalResult(false, null, "sin requires 1 arg");
                            BigDecimal a = st.pop();
                            double rd = Math.sin(a.doubleValue());
                            st.push(new BigDecimal(rd, mathContext));
                        }
                        case "cos" -> {
                            if (st.isEmpty()) return new EvalResult(false, null, "cos requires 1 arg");
                            BigDecimal a = st.pop();
                            double rd = Math.cos(a.doubleValue());
                            st.push(new BigDecimal(rd, mathContext));
                        }
                        case "tan" -> {
                            if (st.isEmpty()) return new EvalResult(false, null, "tan requires 1 arg");
                            BigDecimal a = st.pop();
                            double rd = Math.tan(a.doubleValue());
                            st.push(new BigDecimal(rd, mathContext));
                        }
                        case "sqrt" -> {
                            if (st.isEmpty()) return new EvalResult(false, null, "sqrt requires 1 arg");
                            BigDecimal a = st.pop();
                            if (a.compareTo(BigDecimal.ZERO) < 0) return new EvalResult(false, null, "sqrt domain error");
                            double rd = Math.sqrt(a.doubleValue());
                            st.push(new BigDecimal(rd, mathContext));
                        }
                        case "log" -> {
                            if (st.isEmpty()) return new EvalResult(false, null, "log requires 1 arg");
                            BigDecimal a = st.pop();
                            if (a.compareTo(BigDecimal.ZERO) <= 0) return new EvalResult(false, null, "log domain error");
                            double rd = Math.log10(a.doubleValue());
                            st.push(new BigDecimal(rd, mathContext));
                        }
                        case "ln" -> {
                            if (st.isEmpty()) return new EvalResult(false, null, "ln requires 1 arg");
                            BigDecimal a = st.pop();
                            if (a.compareTo(BigDecimal.ZERO) <= 0) return new EvalResult(false, null, "ln domain error");
                            double rd = Math.log(a.doubleValue());
                            st.push(new BigDecimal(rd, mathContext));
                        }
                        case "abs" -> {
                            if (st.isEmpty()) return new EvalResult(false, null, "abs requires 1 arg");
                            BigDecimal a = st.pop();
                            st.push(a.abs(mathContext));
                        }
                        case "pow" -> {
                            if (st.size() < 2) return new EvalResult(false, null, "pow requires 2 args");
                            BigDecimal b = st.pop();
                            BigDecimal a = st.pop();
                            double rd = Math.pow(a.doubleValue(), b.doubleValue());
                            st.push(new BigDecimal(rd, mathContext));
                        }
                        default -> { return new EvalResult(false, null, "Unknown function: " + t); }
                    }
                } else if ("!".equals(t)) {
                    if (st.isEmpty()) return new EvalResult(false, null, "factorial requires 1 arg");
                    BigDecimal a = st.pop();
                    try {
                        int n = a.stripTrailingZeros().intValueExact();
                        if (n < 0) return new EvalResult(false, null, "factorial domain error");
                        if (n > 170) return new EvalResult(false, null, "factorial too large");
                        BigInteger bi = BigInteger.ONE;
                        for (int i = 2; i <= n; i++) bi = bi.multiply(BigInteger.valueOf(i));
                        st.push(new BigDecimal(bi, mathContext));
                    } catch (ArithmeticException ex) {
                        return new EvalResult(false, null, "factorial requires integer");
                    }
                } else {
                    return new EvalResult(false, null, "Unknown RPN token: " + t);
                }
            }
            if (st.size() != 1)
                return new EvalResult(false, null, "Malformed expression");
            return new EvalResult(true, st.pop(), null);
        } catch (Exception ex) {
            return new EvalResult(false, null, ex.getMessage());
        }
    }

    /**
     * Full pipeline: tokenize -> toRPN -> evalRPN
     */
    public EvalResult evaluateExpression(String s) {
        try {
            var tokens = tokenize(s);
            var rpn = toRPN(tokens);
            return evalRPN(rpn);
        } catch (IllegalArgumentException ex) {
            return new EvalResult(false, null, ex.getMessage());
        }
    }

    /**
     * Simple formatter for display.
     */
    public static String format(BigDecimal v) {
        if (v == null)
            return "";
        BigDecimal rounded = v.stripTrailingZeros();
        String s;
        try {
            s = rounded.toPlainString();
        } catch (Exception e) {
            s = v.toEngineeringString();
        }
        if (s.length() > 20)
            s = v.toEngineeringString();
        return s;
    }
}
