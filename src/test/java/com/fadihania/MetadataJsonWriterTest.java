package com.fadihania;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MetadataJsonWriterTest {
    @Test
    void writesExpectedJsonShape() {
        RepoMetadata metadata = new RepoMetadata(
                "opensourceBIM/BIMserver",
                "https://github.com/opensourceBIM/BIMserver",
                List.of(new ModelFileMetadata(
                        "models.ecore",
                        1,
                        List.of(new VersionMetadata(
                                "2022-10-13T14:09:52Z",
                                "https://raw.githubusercontent.com/opensourceBIM/BIMserver/abcdef/BimServer/models/models.ecore",
                                13530)))));

        String json = MetadataJsonWriter.toJson(metadata);

        assertTrue(json.contains("\"repo_name\": \"opensourceBIM/BIMserver\""));
        assertTrue(json.contains("\"url\": \"https://github.com/opensourceBIM/BIMserver\""));
        assertTrue(json.contains("\"file_name\": \"models.ecore\""));
        assertTrue(json.contains("\"commits\": 1"));
        assertTrue(json.contains("\"date\": \"2022-10-13T14:09:52Z\""));
        assertTrue(json.contains("\"size\": 13530"));
    }
}
