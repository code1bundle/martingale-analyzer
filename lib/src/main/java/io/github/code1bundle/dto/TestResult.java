package io.github.code1bundle.dto;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record TestResult(
        @NonNull String actualOutput,
        @NonNull TestCase testCase
){}
