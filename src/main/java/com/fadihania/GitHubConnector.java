package com.fadihania;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;

final class GitHubConnector {
    private GitHubConnector() {
    }

    static GitHub fromEnvironment() throws IOException {
        String token = System.getenv("GITHUB_TOKEN");
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("GITHUB_TOKEN environment variable is required.");
        }
        return new GitHubBuilder().withOAuthToken(token.trim()).build();
    }
}
