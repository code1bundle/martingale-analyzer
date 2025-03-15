package io.github.code1bundle.analyzer.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class AnalysisResult {
    String successPercentage;
    boolean isValidMartingale;
    boolean isFair;
    List<String> trends;
}