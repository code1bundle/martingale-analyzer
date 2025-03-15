package io.github.code1bundle.analyzer;

import io.github.code1bundle.analyzer.checkers.FairnessChecker;
import io.github.code1bundle.analyzer.checkers.MartingaleValidator;
import io.github.code1bundle.analyzer.checkers.StatisticalEvaluator;
import io.github.code1bundle.analyzer.checkers.TrendDetector;
import io.github.code1bundle.analyzer.dto.AnalysisResult;
import io.github.code1bundle.exec_engine.dto.TestResult;
import lombok.AllArgsConstructor;
import java.util.List;

@AllArgsConstructor
public class Analyzer {
    private StatisticalEvaluator statisticalEvaluator;
    private MartingaleValidator martingaleValidator;
    private FairnessChecker fairnessChecker;
    private TrendDetector trendDetector;

    public AnalysisResult analyze(List<TestResult> testResults) {
        AnalysisResult analysisResult = new AnalysisResult();

        // Compare actual vs expected outputs and write percentage

        // Validate martingale properties

        // Check fairness

        // Detect trends

        return analysisResult;
    }
}
