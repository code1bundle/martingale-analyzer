package io.github.code1bundle.exec_engine.dto;

import io.github.code1bundle.loaders.dto.TestCase;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record TestResult(
        @NonNull Object actualOutput,
        @NonNull TestCase testCase
){}
