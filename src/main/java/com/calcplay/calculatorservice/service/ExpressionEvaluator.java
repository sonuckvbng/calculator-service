package com.calcplay.calculatorservice.service;

import com.calcplay.calculatorservice.exception.InvalidExpressionException;
import java.util.Locale;

/** Bounded recursive-descent parser. User input is never executed as source code. */
final class ExpressionEvaluator {
    private String input;
    private int pos;
    private int operations;

    double evaluate(String expression) {
        if (expression == null || expression.isBlank() || expression.length() > 300) {
            throw new InvalidExpressionException("Enter an expression up to 300 characters.");
        }
        input = expression.replace("×", "*").replace("÷", "/").replace("−", "-");
        pos = 0;
        operations = 0;
        double value = parseExpression();
        skipSpaces();
        if (pos != input.length()) {
            throw new InvalidExpressionException("Unexpected input near: " + input.substring(pos));
        }
        checkFinite(value);
        return value;
    }

    private double parseExpression() {
        double value = parseTerm();
        while (true) {
            skipSpaces();
            if (take('+')) value += parseTerm();
            else if (take('-')) value -= parseTerm();
            else return value;
            checkFinite(value);
        }
    }

    private double parseTerm() {
        double value = parsePower();
        while (true) {
            skipSpaces();
            if (take('*')) value *= parsePower();
            else if (take('/')) {
                double divisor = parsePower();
                if (divisor == 0) throw new InvalidExpressionException("Cannot divide by zero.");
                value /= divisor;
            } else if (take('%')) {
                double divisor = parsePower();
                if (divisor == 0) throw new InvalidExpressionException("Cannot take modulo zero.");
                value %= divisor;
            } else return value;
            checkFinite(value);
        }
    }

    private double parsePower() {
        double base = parseUnary();
        skipSpaces();
        if (take('^')) {
            base = Math.pow(base, parsePower());
            checkFinite(base);
        }
        return base;
    }

    private double parseUnary() {
        skipSpaces();
        if (take('+')) return parseUnary();
        if (take('-')) return -parseUnary();
        return parsePrimary();
    }

    private double parsePrimary() {
        skipSpaces();
        if (++operations > 500) throw new InvalidExpressionException("Expression is too complex.");
        if (take('(')) {
            double value = parseExpression();
            skipSpaces();
            if (!take(')')) throw new InvalidExpressionException("Missing closing parenthesis.");
            return value;
        }
        if (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
            return parseNumber();
        }
        String word = identifier();
        if (!word.isEmpty()) {
            if (word.equals("pi")) return Math.PI;
            if (word.equals("e")) return Math.E;
            skipSpaces();
            if (!take('(')) throw new InvalidExpressionException("Function requires parentheses: " + word);
            double argument = parseExpression();
            skipSpaces();
            if (!take(')')) throw new InvalidExpressionException("Missing closing parenthesis.");
            return switch (word) {
                case "sqrt" -> {
                    if (argument < 0) throw new InvalidExpressionException("Square root requires a non-negative value.");
                    yield Math.sqrt(argument);
                }
                case "sin" -> Math.sin(argument);
                case "cos" -> Math.cos(argument);
                case "tan" -> Math.tan(argument);
                case "log" -> {
                    if (argument <= 0) throw new InvalidExpressionException("log requires a positive value.");
                    yield Math.log10(argument);
                }
                case "ln" -> {
                    if (argument <= 0) throw new InvalidExpressionException("ln requires a positive value.");
                    yield Math.log(argument);
                }
                default -> throw new InvalidExpressionException("Unsupported function: " + word);
            };
        }
        throw new InvalidExpressionException("Expected a number, constant, or parenthesized expression.");
    }

    private double parseNumber() {
        int start = pos;
        boolean dot = false;
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isDigit(c)) pos++;
            else if (c == '.' && !dot) { dot = true; pos++; }
            else break;
        }
        try {
            return Double.parseDouble(input.substring(start, pos));
        } catch (NumberFormatException ex) {
            throw new InvalidExpressionException("Invalid number.");
        }
    }

    private String identifier() {
        skipSpaces();
        int start = pos;
        while (pos < input.length() && Character.isLetter(input.charAt(pos))) pos++;
        return input.substring(start, pos).toLowerCase(Locale.ROOT);
    }

    private boolean take(char c) {
        if (pos < input.length() && input.charAt(pos) == c) { pos++; return true; }
        return false;
    }

    private void skipSpaces() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) pos++;
    }

    private void checkFinite(double value) {
        if (!Double.isFinite(value)) throw new InvalidExpressionException("Result is outside the supported range.");
    }
}
