package io.github.code1bundle.exec_engine;

import io.github.code1bundle.dto.TestResult;
import io.github.code1bundle.loaders.CodeLoader;
import io.github.code1bundle.dto.TestCase;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@RequiredArgsConstructor
public class TestRunner {
    private final CodeExecutor codeExecutor;

    public List<TestResult> runTests(List<TestCase> testCases, String codeSourcesFilePath) throws Exception {
        List<TestResult> testResults = new ArrayList<>();
        String functionCode = CodeLoader.loadFromFile(codeSourcesFilePath);
        for (TestCase testCase : testCases) {
            Map<String, String> parameters = testCase.input();
            String output = codeExecutor.executeFunction(functionCode, parameters);
            System.out.println("Raw output for test case " + testCase + ": " + output);
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