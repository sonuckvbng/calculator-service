package com.calcplay.calculatorservice.service;

import com.calcplay.calculatorservice.exception.InvalidExpressionException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculationServiceTest {
    private final CalculationService service = new CalculationService();
    @Test void respectsPrecedence() { assertEquals("14", service.evaluate("2 + 3 * 4")); }
    @Test void supportsParentheses() { assertEquals("20", service.evaluate("(2 + 3) * 4")); }
    @Test void supportsScientificFunction() { assertEquals("9", service.evaluate("sqrt(81)")); }
    @Test void rejectsDivisionByZero() { assertThrows(InvalidExpressionException.class, () -> service.evaluate("1/0")); }
    @Test void rejectsCodeLikeInput() { assertThrows(InvalidExpressionException.class, () -> service.evaluate("Runtime.exec(1)")); }
}
