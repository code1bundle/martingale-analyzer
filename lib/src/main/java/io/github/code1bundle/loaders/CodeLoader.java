package io.github.code1bundle.loaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class CodeLoader {

    public static String loadFromFile(String filePath) throws IOException {
        StringBuilder code = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                code.append(line).append("\n");
            }
        }
        return code.toString();
    }

//    public String loadFromUrl(String urlString) throws IOException {
//        // TODO URL
//        URL url = new URL(urlString);
//        return new String(url.openStream().readAllBytes());
//    }

//    public String loadFromGit(String repoUrl, String branch, String filePath) throws Exception {
//        // TODO JGIT
//        Path tempDir = Files.createTempDirectory("repo");
//        Path codeFile = tempDir.resolve(filePath);
//        return Files.readString(codeFile);
//    }
}
