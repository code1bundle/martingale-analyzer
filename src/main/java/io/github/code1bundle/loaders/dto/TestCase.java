package io.github.code1bundle.loaders.dto;

import lombok.Builder;
import lombok.NonNull;
import java.util.Map;

@Builder
public record TestCase(
  @NonNull Map<String, String> input,
  @NonNull Object expectedOutput,
  String description
){}
