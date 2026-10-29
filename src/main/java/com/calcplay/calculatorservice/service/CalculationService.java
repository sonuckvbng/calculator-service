package com.calcplay.calculatorservice.service;

import com.calcplay.calculatorservice.exception.InvalidExpressionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class CalculationService {
    private static final Logger log = LoggerFactory.getLogger(CalculationService.class);
    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();

    public String evaluate(String expression) {
        log.debug("Evaluating expression: {}", expression);
        try {
            double value = evaluator.evaluate(expression);
            log.debug("Raw evaluation result: {}", value);
            if (!Double.isFinite(value)) {
                log.warn("Result is outside supported range: {}", value);
                throw new InvalidExpressionException("Result is outside the supported range.");
            }
            String result;
            if (value == Math.rint(value) && Math.abs(value) < 1e15) {
                result = String.format(Locale.ROOT, "%.0f", value);
            } else {
                result = String.format(Locale.ROOT, "%.12f", value)
                        .replaceAll("0+$", "")
                        .replaceAll("\\.$", "");
            }
            log.debug("Formatted result: {}", result);
            return result;
        } catch (InvalidExpressionException ex) {
            log.error("Invalid expression: {} - {}", expression, ex.getMessage());
            throw ex;
        } catch (IllegalArgumentException ex) {
            log.error("Illegal argument for expression: {} - {}", expression, ex.getMessage());
            throw new InvalidExpressionException(ex.getMessage());
        }
    }
}
