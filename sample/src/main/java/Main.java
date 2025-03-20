import io.github.code1bundle.analyzer.Analyzer;
import io.github.code1bundle.dto.AnalysisResult;
import io.github.code1bundle.dto.TestResult;
import io.github.code1bundle.exec_engine.CodeExecutor;
import io.github.code1bundle.exec_engine.DockerManager;
import io.github.code1bundle.exec_engine.TestRunner;
import io.github.code1bundle.dto.TestCase;
import io.github.code1bundle.analyzer.checkers.StatisticalEvaluator;
import io.github.code1bundle.analyzer.checkers.MartingaleValidator;
import io.github.code1bundle.analyzer.checkers.FairnessChecker;
import io.github.code1bundle.analyzer.checkers.TrendDetector;
import io.github.code1bundle.loaders.TestLoader;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Load test cases from a file (e.g., JSON)
            TestLoader testLoader = new TestLoader();
            List<TestCase> testCases = testLoader.loadTestCasesFromJson("sample/src/main/resources/test_cases.json");

            // Step 2: Initialize Docker and execution components
            DockerManager dockerManager = new DockerManager();
            CodeExecutor codeExecutor = new CodeExecutor(dockerManager, "python");
            TestRunner testRunner = new TestRunner(codeExecutor);

            // Step 3: Run tests and collect results
            List<TestResult> testResults = testRunner.runTests(testCases, "sample/src/main/resources/app.py");

            // Step 4: Initialize analyzer with all validators
            Analyzer analyzer = new Analyzer(
                    new StatisticalEvaluator(),
                    new MartingaleValidator(),
                    new FairnessChecker(),
                    new TrendDetector()
            );

            // Step 5: Perform analysis
            AnalysisResult analysisResult = analyzer.analyze(testResults);

            // Step 6: Output results (using console reporting)
            System.out.println("Analysis Results:");
            System.out.println("Success Percentage: " + analysisResult.getSuccessPercentage());
            System.out.println("Martingale Valid: " + analysisResult.isValidMartingale());
            System.out.println("Fairness Check: " + analysisResult.isFair());
            System.out.println("Trends/Outliers: " + String.join(", ", analysisResult.getTrends()));

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
