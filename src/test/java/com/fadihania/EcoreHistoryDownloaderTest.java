package com.fadihania;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EcoreHistoryDownloaderTest {
    @Test
    void generatesVersionFileNameWithUtcDateAndShortSha() {
        String name = EcoreHistoryDownloader.versionFileName(
                "models.ecore",
                Instant.parse("2022-10-13T14:09:52Z"),
                "a1b2c3d4e5f6");

        assertEquals("models_2022-10-13_a1b2c3d.ecore", name);
    }

    @Test
    void keepsShortShaWhenShaHasSevenCharacters() {
        String name = EcoreHistoryDownloader.versionFileName(
                "models.ecore",
                Instant.parse("2022-10-13T14:09:52Z"),
                "a1b2c3d");

        assertEquals("models_2022-10-13_a1b2c3d.ecore", name);
    }

    @Test
    void generatesRawContentUrl() {
        String url = EcoreHistoryDownloader.rawContentUrl(
                "opensourceBIM/BIMserver",
                "abcdef123456",
                "BimServer/models/models.ecore");

        assertEquals("https://raw.githubusercontent.com/opensourceBIM/BIMserver/abcdef123456/BimServer/models/models.ecore", url);
    }
}
