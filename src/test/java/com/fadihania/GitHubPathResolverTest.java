package com.fadihania;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GitHubPathResolverTest {
    @Test
    void extractsPathFromBlobUrl() {
        String path = GitHubPathResolver.repositoryPath(
                "https://github.com/opensourceBIM/BIMserver/blob/master/BimServer/models/models.ecore",
                "opensourceBIM/BIMserver");

        assertEquals("BimServer/models/models.ecore", path);
    }

    @Test
    void extractsPathFromRawUrl() {
        String path = GitHubPathResolver.repositoryPath(
                "https://raw.githubusercontent.com/opensourceBIM/BIMserver/master/BimServer/models/models.ecore",
                "opensourceBIM/BIMserver");

        assertEquals("BimServer/models/models.ecore", path);
    }

    @Test
    void acceptsRepositoryRelativePath() {
        String path = GitHubPathResolver.repositoryPath(
                "BimServer/models/models.ecore",
                "opensourceBIM/BIMserver");

        assertEquals("BimServer/models/models.ecore", path);
    }
}
