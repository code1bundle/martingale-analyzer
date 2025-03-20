package io.github.code1bundle.exec_engine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CodeExecutor {
    private final DockerManager dockerManager;
    @Getter
    private final String language;
    private final static Map<String, String> commandTemplates = new HashMap<>(){{
        put("python", "python -c 'import sys; sys.stderr = open(\"/dev/null\", \"w\"); ");
        put("javascript", "node -e 'process.stderr.write = () => {}; ");
        put("ruby", "ruby -e 'STDERR.reopen('/dev/null'); ");
        put("cpp", "./compiled_code");
        put("bash", "bash -c 'exec 2>/dev/null; ");
    }};

    public String executeFunction(String language, String functionCode, Map<String, String> parameters) throws Exception {
        String imageName = "code-executor-" + language;
        String dockerfilePath = "exec_engine/dockerfile/" + language + "/Dockerfile";

        dockerManager.buildImage(dockerfilePath, imageName);

        String commandTemplate = commandTemplates.get(language);
        List<String> command = List.of(commandTemplate, functionCode);

        List<String> envVars = parameters.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .toList();

        // TODO CLEAR LOGS OF CONTAINER SO THE ACTUAL OUTPUT WILL BE ACCESSIBLE
        return dockerManager.launchContainer(imageName, command, envVars);
    }
}
