package io.github.code1bundle.analyzer.checkers;

import io.github.code1bundle.dto.TestResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TrendDetector {
    private static final double SMOOTHING_FACTOR = 0.3; // EMA smoothing factor

    /**
     * Detects outliers using EMA and standard deviation
     */
    public List<String> detectTrends(List<TestResult> testResults) {
        List<Double> values = testResults.stream()
                .map(TestResult::actualOutput)
                .map(Double::parseDouble)
                .collect(Collectors.toList());

        if (values.size() < 2) return List.of(); // Insufficient data for analysis

        double ema = calculateEMA(values);
        double stdDev = calculateStandardDeviation(values, ema);

        List<String> trends = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            if (Math.abs(values.get(i) - ema) > 2 * stdDev) {
                trends.add(String.format("Outlier at position %d: %f", i, values.get(i)));
            }
        }
        return trends;
    }

    // Exponential Moving Average
    private double calculateEMA(List<Double> values) {
        double ema = values.getFirst();
        for (int i = 1; i < values.size(); i++) {
            ema = (values.get(i) * SMOOTHING_FACTOR) + (ema * (1 - SMOOTHING_FACTOR));
        }
        return ema;
    }

    // Standard deviation
    private double calculateStandardDeviation(List<Double> values, double mean) {
        double sumSquaredDiff = 0.0;
        for (Double value : values) {
            sumSquaredDiff += Math.pow(value - mean, 2);
        }
        return Math.sqrt(sumSquaredDiff / (values.size() - 1));
    }
}
