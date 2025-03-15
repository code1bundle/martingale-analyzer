package io.github.code1bundle.exec_engine;

import io.github.code1bundle.exec_engine.dto.TestResult;
import io.github.code1bundle.loaders.CodeLoader;
import io.github.code1bundle.loaders.dto.TestCase;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@RequiredArgsConstructor
public class TestRunner {
    private final CodeExecutor codeExecutor;

    public List<TestResult> runTests(List<TestCase> testCases, String codeSourcesFilePath) throws Exception {
        List<TestResult> testResults = new ArrayList<>();
        for (TestCase testCase : testCases) {
            String functionCode = CodeLoader.loadFromFile(codeSourcesFilePath);
            Map<String, String> parameters = testCase.input();
            String output = codeExecutor.executeFunction(codeExecutor.getLanguage(), functionCode, parameters);
            testResults
                    .add(
                            TestResult
                                    .builder()
                                    .testCase(testCase)
                                    .actualOutput(output)
                                    .build()
                    );
        }
        return testResults;
    }
}