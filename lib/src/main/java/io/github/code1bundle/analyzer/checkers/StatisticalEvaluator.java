package io.github.code1bundle.analyzer.checkers;

import io.github.code1bundle.dto.TestResult;

import java.util.List;

public class StatisticalEvaluator {
    private static final double TOLERANCE = 0.01; // Acceptable margin of error for numeric comparisons
    private static final double Z_SCORE_95_CONFIDENCE = 1.96; // Critical value for 95% confidence interval

    /**
     * Calculates the pass rate by comparing actual vs expected outputs with tolerance
     */
    public double calculatePassPercentage(List<TestResult> testResults) {
        if (testResults.isEmpty()) return 0.0;

        long passed = testResults.stream()
                .filter(tr -> {
                    double actual = parseOutput(tr.actualOutput());
                    double expected = parseOutput(tr.testCase().expectedOutput());
                    return Math.abs(actual - expected) <= TOLERANCE;
                })
                .count();

        return (double) passed / testResults.size() * 100;
    }

    /**
     * Checks if the mean of results significantly differs from the expected value using Z-test
     */
    public boolean isStatisticallySignificant(List<Double> results, double expectedValue) {
        if (results.size() < 2) return false; // Minimum 2 samples required

        double mean = calculateMean(results);
        double stdDev = calculateStandardDeviation(results);

        // Calculate Z-score for sample mean
        double zScore = (mean - expectedValue) / (stdDev / Math.sqrt(results.size()));
        return Math.abs(zScore) > Z_SCORE_95_CONFIDENCE;
    }

    private double parseOutput(String output) {
        try {
            return Double.parseDouble(output);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Non-numeric output detected: " + output);
        }
    }

    private double calculateMean(List<Double> values) {
        return values.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElseThrow(() -> new IllegalArgumentException("Empty results"));
    }

    private double calculateStandardDeviation(List<Double> values) {
        double mean = calculateMean(values);
        double squaredDiffs = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .sum();
        return Math.sqrt(squaredDiffs / (values.size() - 1));
    }
}