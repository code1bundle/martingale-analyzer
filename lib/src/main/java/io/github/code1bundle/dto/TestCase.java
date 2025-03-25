package io.github.code1bundle.dto;

import lombok.Builder;
import lombok.NonNull;
import java.util.Map;

@Builder
public record TestCase(
        @NonNull Map<String, String> input,
        @NonNull String expectedOutput,
        String description
){}
