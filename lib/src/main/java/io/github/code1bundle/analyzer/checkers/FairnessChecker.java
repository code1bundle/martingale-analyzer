package io.github.code1bundle.analyzer.checkers;

import io.github.code1bundle.dto.TestResult;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FairnessChecker {
    private static final double FAIRNESS_THRESHOLD = 0.8; // 80% rule for disparate impact
    private static final double TOLERANCE = 0.01; // Match StatisticalEvaluator's tolerance

    /**
     * Checks fairness using the 80% rule for disparate impact.
     */
    public boolean checkFairness(List<TestResult> testResults) {
        // Group results by sensitive attribute (e.g., "group" in input)
        Map<String, List<TestResult>> groups = testResults.stream()
                .collect(Collectors.groupingBy(
                        tr -> tr.testCase().input().getOrDefault("group", "default")
                ));

        if (groups.size() < 2) return true; // No groups to compare

        // Calculate pass rates for each group
        Map<String, Double> groupPassRates = groups.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> calculatePassRate(entry.getValue())
                ));

        // Apply 80% rule: minPassRate / maxPassRate >= 0.8
        double minPassRate = groupPassRates.values().stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double maxPassRate = groupPassRates.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);

        return (minPassRate / maxPassRate) >= FAIRNESS_THRESHOLD;
    }

    private double calculatePassRate(List<TestResult> groupResults) {
        long passed = groupResults.stream()
                .filter(tr -> {
                    double actual = parseOutput(tr.actualOutput());
                    double expected = parseOutput(tr.testCase().expectedOutput());
                    return Math.abs(actual - expected) <= TOLERANCE;
                })
                .count();
        return (double) passed / groupResults.size();
    }

    private double parseOutput(String output) {
        try {
            return Double.parseDouble(output);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Non-numeric output: " + output);
        }
    }
}