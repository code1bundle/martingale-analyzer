package io.github.code1bundle.analyzer;

import io.github.code1bundle.analyzer.checkers.FairnessChecker;
import io.github.code1bundle.analyzer.checkers.MartingaleValidator;
import io.github.code1bundle.analyzer.checkers.StatisticalEvaluator;
import io.github.code1bundle.analyzer.checkers.TrendDetector;
import io.github.code1bundle.dto.AnalysisResult;
import io.github.code1bundle.dto.TestResult;
import lombok.AllArgsConstructor;
import java.util.List;

@AllArgsConstructor
public class Analyzer {
    private final StatisticalEvaluator statisticalEvaluator;
    private final MartingaleValidator martingaleValidator;
    private final FairnessChecker fairnessChecker;
    private final TrendDetector trendDetector;

    public AnalysisResult analyze(List<TestResult> testResults) {
        AnalysisResult analysisResult = new AnalysisResult();

        // Calculate pass percentage with tolerance
        double passPercentage = statisticalEvaluator.calculatePassPercentage(testResults);
        analysisResult.setSuccessPercentage(String.format("%.2f%%", passPercentage));

        // Validate martingale property (sequential stability)
        boolean isValidMartingale = martingaleValidator.validateMartingale(testResults);
        analysisResult.setValidMartingale(isValidMartingale);

        // Check fairness using 80% rule for disparate impact
        boolean isFair = fairnessChecker.checkFairness(testResults);
        analysisResult.setFair(isFair);

        // Detect trends and outliers
        List<String> trends = trendDetector.detectTrends(testResults);
        analysisResult.setTrends(trends);

        return analysisResult;
    }
}
