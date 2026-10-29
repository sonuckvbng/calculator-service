package com.calcplay.calculatorservice.controller;

import com.calcplay.calculatorservice.dto.EvaluationRequest;
import com.calcplay.calculatorservice.dto.EvaluationResponse;
import com.calcplay.calculatorservice.service.CalculationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/calculations")
public class CalculationController {
    private static final Logger log = LoggerFactory.getLogger(CalculationController.class);
    private final CalculationService calculationService;

    public CalculationController(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @PostMapping("/evaluate")
    public EvaluationResponse evaluate(@Valid @RequestBody EvaluationRequest request) {
        log.info("Received evaluation request for expression: {}", request.expression());
        String result = calculationService.evaluate(request.expression());
        EvaluationResponse response = new EvaluationResponse(request.expression(), result, true);
        log.info("Evaluation completed - expression: {}, result: {}", request.expression(), result);
        return response;
    }
}
