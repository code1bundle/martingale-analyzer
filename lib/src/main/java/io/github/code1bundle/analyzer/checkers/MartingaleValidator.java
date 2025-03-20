package io.github.code1bundle.analyzer.checkers;

import io.github.code1bundle.dto.TestResult;

import java.util.List;

public class MartingaleValidator {
    private static final double TOLERANCE = 0.05; // Allow ±5% deviation

    /**
     * Validates if the sequence of results follows the martingale property.
     */
    public boolean validateMartingale(List<TestResult> testResults) {
        if (testResults.size() < 2) return false; // Need at least 2 results

        for (int i = 1; i < testResults.size(); i++) {
            double prev = parseOutput(testResults.get(i - 1).actualOutput());
            double current = parseOutput(testResults.get(i).actualOutput());

            // Check if current result deviates from previous by more than tolerance
            if (Math.abs(current - prev) > TOLERANCE) {
                return false;
            }
        }
        return true;
    }

    private double parseOutput(String output) {
        try {
            return Double.parseDouble(output);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Non-numeric output: " + output);
        }
    }
}