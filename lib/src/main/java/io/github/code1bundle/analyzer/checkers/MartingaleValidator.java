package io.github.code1bundle.analyzer.checkers;

import io.github.code1bundle.dto.TestResult;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MartingaleValidator {
    private static final double TOLERANCE = 0.05; // Allow ±5% deviation for *expected* changes

    public boolean validateMartingale(List<TestResult> testResults) {
        if (testResults.size() < 2) {
            return true; // Not enough data to disprove martingale property
        }

        // Group test results by their *expected* change.  This is crucial.
        //  We assume a convention where the expected change is somehow encoded in the
        //  input parameters.  For the random walk, this could be implicit (expected change is 0).
        //  For the stock price simulation, we'd need to *calculate* the expected change
        //  based on the 'drift' parameter.  This is a simplification; a real martingale
        //  test would be more sophisticated.

        // Example (simplified): Assuming 'drift' is a parameter and represents the expected *percentage* change.
        Map<Double, List<TestResult>> groupedByExpectedChange = testResults.stream()
                .collect(Collectors.groupingBy(tr -> {
                    // **IMPORTANT:**  This assumes your input parameters somehow encode the expected change.
                    // You might need to adjust this logic.  For example, if you're using the random walk,
                    // the expected change is *always* 0.
                    if (tr.testCase().input().containsKey("DRIFT")) { // Example for stock price simulation
                        return Double.parseDouble(tr.testCase().input().get("DRIFT"));
                    } else {
                        return 0.0;  // Assume 0 expected change if no "DRIFT" parameter (e.g., for random walk)
                    }
                }));

        // Now, for *each group* with the same expected change, check if the *average actual change*
        // is within tolerance of the expected change.
        for (Map.Entry<Double, List<TestResult>> entry : groupedByExpectedChange.entrySet()) {
            double expectedChange = entry.getKey();
            List<TestResult> resultsForThisExpectedChange = entry.getValue();

            if (resultsForThisExpectedChange.size() < 2) {
                continue; // Not enough data for this group
            }

            // Calculate the *average actual change* for this group.
            double sumOfActualChanges = 0;
            for (int i = 1; i < resultsForThisExpectedChange.size(); i++) {
                double prevValue = parseOutput(resultsForThisExpectedChange.get(i - 1).actualOutput());
                double currentValue = parseOutput(resultsForThisExpectedChange.get(i).actualOutput());
                // Calculate *percentage* change:
                sumOfActualChanges += (currentValue - prevValue) / prevValue;
            }
            double averageActualChange = sumOfActualChanges / (resultsForThisExpectedChange.size() - 1);


            // Check if the average actual change is within tolerance of the expected change.
            if (Math.abs(averageActualChange - expectedChange) > TOLERANCE) {
                return false; // Martingale property violated for this group
            }
        }

        return true; // Martingale property holds for all groups (within tolerance)
    }

    private double parseOutput(String output) {
        try {
            return Double.parseDouble(output);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Non-numeric output: " + output);
        }
    }
}