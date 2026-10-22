package com.fadihania;

import java.net.URI;
import java.net.URISyntaxException;

final class GitHubPathResolver {
    private GitHubPathResolver() {
    }

    static String repositoryPath(String fileUrl, String repositoryFullName) {
        String trimmed = fileUrl.trim();
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            return stripLeadingSlash(trimmed);
        }

        try {
            URI uri = new URI(trimmed);
            String host = uri.getHost();
            String path = stripLeadingSlash(uri.getPath());

            if ("github.com".equalsIgnoreCase(host)) {
                return fromGitHubBlobPath(path, repositoryFullName);
            }
            if ("raw.githubusercontent.com".equalsIgnoreCase(host)) {
                return fromRawGitHubPath(path, repositoryFullName);
            }
            throw new IllegalArgumentException("Unsupported GitHub file URL host: " + host);
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException("Malformed file URL: " + fileUrl, exception);
        }
    }

    private static String fromGitHubBlobPath(String path, String repositoryFullName) {
        String prefix = repositoryFullName + "/blob/";
        if (!path.startsWith(prefix)) {
            throw new IllegalArgumentException("File URL path does not match repository " + repositoryFullName + ": " + path);
        }

        String afterBlob = path.substring(prefix.length());
        int branchEnd = afterBlob.indexOf('/');
        if (branchEnd < 0 || branchEnd == afterBlob.length() - 1) {
            throw new IllegalArgumentException("GitHub blob URL does not contain a file path: " + path);
        }
        return afterBlob.substring(branchEnd + 1);
    }

    private static String fromRawGitHubPath(String path, String repositoryFullName) {
        String prefix = repositoryFullName + "/";
        if (!path.startsWith(prefix)) {
            throw new IllegalArgumentException("Raw file URL path does not match repository " + repositoryFullName + ": " + path);
        }

        String afterRepo = path.substring(prefix.length());
        int refEnd = afterRepo.indexOf('/');
        if (refEnd < 0 || refEnd == afterRepo.length() - 1) {
            throw new IllegalArgumentException("Raw file URL does not contain a file path: " + path);
        }
        return afterRepo.substring(refEnd + 1);
    }

    private static String stripLeadingSlash(String value) {
        String stripped = value;
        while (stripped.startsWith("/")) {
            stripped = stripped.substring(1);
        }
        return stripped;
    }
}
