package com.fadihania;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

final class MetadataJsonWriter {
    private MetadataJsonWriter() {
    }

    static void write(Path path, RepoMetadata metadata) throws IOException {
        Files.writeString(path, toJson(metadata), StandardCharsets.UTF_8);
    }

    static String toJson(RepoMetadata metadata) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        appendField(json, 1, "repo_name", metadata.repoName(), true);
        appendField(json, 1, "url", metadata.url(), true);
        indent(json, 1).append("\"model_files\": [\n");

        for (int fileIndex = 0; fileIndex < metadata.modelFiles().size(); fileIndex++) {
            ModelFileMetadata modelFile = metadata.modelFiles().get(fileIndex);
            indent(json, 2).append("{\n");
            appendField(json, 3, "file_name", modelFile.fileName(), true);
            indent(json, 3).append("\"commits\": ").append(modelFile.commits()).append(",\n");
            indent(json, 3).append("\"versions\": [\n");

            for (int versionIndex = 0; versionIndex < modelFile.versions().size(); versionIndex++) {
                VersionMetadata version = modelFile.versions().get(versionIndex);
                indent(json, 4).append("{\n");
                appendField(json, 5, "date", version.date(), true);
                appendField(json, 5, "url", version.url(), true);
                indent(json, 5).append("\"size\": ").append(version.size()).append('\n');
                indent(json, 4).append("}");
                if (versionIndex < modelFile.versions().size() - 1) {
                    json.append(',');
                }
                json.append('\n');
            }

            indent(json, 3).append("]\n");
            indent(json, 2).append("}");
            if (fileIndex < metadata.modelFiles().size() - 1) {
                json.append(',');
            }
            json.append('\n');
        }

        indent(json, 1).append("]\n");
        json.append("}\n");
        return json.toString();
    }

    private static void appendField(StringBuilder json, int depth, String name, String value, boolean comma) {
        indent(json, depth)
                .append('"').append(escape(name)).append("\": ")
                .append('"').append(escape(value)).append('"');
        if (comma) {
            json.append(',');
        }
        json.append('\n');
    }

    private static StringBuilder indent(StringBuilder json, int depth) {
        return json.append("  ".repeat(depth));
    }

    private static String escape(String value) {
        StringBuilder escaped = new StringBuilder(value.length());
        for (int index = 0; index < value.length(); index++) {
            char ch = value.charAt(index);
            switch (ch) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (ch < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) ch));
                    } else {
                        escaped.append(ch);
                    }
                }
            }
        }
        return escaped.toString();
    }
}
