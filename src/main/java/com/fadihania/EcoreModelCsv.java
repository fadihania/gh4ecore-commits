package com.fadihania;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

final class EcoreModelCsv {
    private static final int EXPECTED_COLUMNS = 5;

    private EcoreModelCsv() {
    }

    static List<ModelFileRequest> read(Path path) throws IOException {
        List<ModelFileRequest> requests = new ArrayList<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index).trim();
            if (line.isEmpty()) {
                continue;
            }
            requests.add(parseLine(line, index + 1));
        }

        if (requests.isEmpty()) {
            throw new IllegalArgumentException("No model rows found in " + path);
        }

        return requests;
    }

    static ModelFileRequest parseLine(String line, int lineNumber) {
        String[] columns = line.split(",", -1);
        if (columns.length != EXPECTED_COLUMNS) {
            throw new IllegalArgumentException("Line " + lineNumber + " must contain 5 CSV columns, found " + columns.length);
        }

        String modelFileName = require(columns[0], "model file name", lineNumber);
        String repositoryFullName = require(columns[1], "repository full name", lineNumber);
        String repositoryUrl = require(columns[2], "repository URL", lineNumber);
        String fileUrl = require(columns[3], "file URL", lineNumber);
        int minimumCommitCount = parseCommitCount(columns[4], lineNumber);

        if (repositoryFullName.indexOf('/') <= 0 || repositoryFullName.indexOf('/') == repositoryFullName.length() - 1) {
            throw new IllegalArgumentException("Line " + lineNumber + " has malformed repository full name: " + repositoryFullName);
        }

        return new ModelFileRequest(modelFileName, repositoryFullName, repositoryUrl, fileUrl, minimumCommitCount);
    }

    private static String require(String value, String columnName, int lineNumber) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Line " + lineNumber + " is missing " + columnName);
        }
        return trimmed;
    }

    private static int parseCommitCount(String value, int lineNumber) {
        String trimmed = require(value, "commit count", lineNumber);
        try {
            int count = Integer.parseInt(trimmed);
            if (count < 1) {
                throw new IllegalArgumentException("Line " + lineNumber + " commit count must be positive");
            }
            return count;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Line " + lineNumber + " commit count is not a number: " + trimmed, exception);
        }
    }
}
