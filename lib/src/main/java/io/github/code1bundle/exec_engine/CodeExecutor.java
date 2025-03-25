package io.github.code1bundle.exec_engine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CodeExecutor {
    private final DockerManager dockerManager;
    @Getter
    private final String language;
    private static final Map<String, String> dockerfileTemplates = new HashMap<>();

    static {
        dockerfileTemplates.put("python",
                """
                FROM python:3.9-slim
                WORKDIR /app
                COPY script.py .
                CMD ["python", "-c", "import sys; sys.stderr=open('/dev/null','w'); exec(open('script.py').read())"]
                """
        );

        dockerfileTemplates.put("javascript",
                """
                FROM node:18-alpine
                WORKDIR /app
                COPY script.js .
                CMD ["node", "script.js"]
                """
        );

        dockerfileTemplates.put("ruby",
                """
                FROM ruby:3.2-slim
                WORKDIR /app
                COPY script.rb .
                CMD ["ruby", "-e", "STDERR.reopen('/dev/null'); load './script.rb'"]
                """
        );

        dockerfileTemplates.put("cpp",
                """
                FROM gcc:latest
                WORKDIR /app
                COPY script.cpp .
                RUN g++ -o compiled_code script.cpp 2>/dev/null
                CMD ["./compiled_code"]
                """
        );

        dockerfileTemplates.put("bash",
                """
                FROM alpine:latest
                WORKDIR /app
                COPY script.sh .
                RUN chmod +x script.sh
                CMD ["bash", "-c", "exec 2>/dev/null; ./script.sh"]
                """
        );
    }

    public String executeFunction(String functionCode, Map<String, String> parameters) throws Exception {
        String imageName = "code-executor-" + language;
        File tempDir = Files.createTempDirectory("docker-build").toFile();

        File codeFile = new File(tempDir, "script." + getExtension(language));
        Files.write(codeFile.toPath(), functionCode.getBytes());

        File dockerfile = new File(tempDir, "Dockerfile");
        Files.write(
                dockerfile.toPath(),
                dockerfileTemplates.get(language).getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.SYNC
        );

        try {
            dockerManager.buildImage(dockerfile.getAbsolutePath(), imageName);
            return dockerManager.launchContainer(imageName, List.of(), parametersToEnvList(parameters));
        } finally {
            deleteDirectory(tempDir);
        }
    }

    private void deleteDirectory(File directory) {
        File[] allContents = directory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directory.delete();
    }

    private List<String> parametersToEnvList(Map<String, String> parameters) {
        return parameters.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .toList();
    }

    private String getExtension(String language) {
        return switch (language) {
            case "python" -> "py";
            case "javascript" -> "js";
            case "ruby" -> "rb";
            case "bash" -> "sh";
            default -> throw new IllegalArgumentException("Unsupported language");
        };
    }
}
