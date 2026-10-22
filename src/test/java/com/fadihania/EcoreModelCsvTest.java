package com.fadihania;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EcoreModelCsvTest {
    @Test
    void parsesFiveColumnRow() {
        ModelFileRequest request = EcoreModelCsv.parseLine(
                "models.ecore,opensourceBIM/BIMserver,https://github.com/opensourceBIM/BIMserver,https://github.com/opensourceBIM/BIMserver/blob/master/BimServer/models/models.ecore,241",
                1);

        assertEquals("models.ecore", request.modelFileName());
        assertEquals("opensourceBIM/BIMserver", request.repositoryFullName());
        assertEquals("https://github.com/opensourceBIM/BIMserver", request.repositoryUrl());
        assertEquals("https://github.com/opensourceBIM/BIMserver/blob/master/BimServer/models/models.ecore", request.fileUrl());
        assertEquals(241, request.minimumCommitCount());
    }

    @Test
    void rejectsMissingColumns() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EcoreModelCsv.parseLine("models.ecore,owner/repo", 7));

        assertEquals("Line 7 must contain 5 CSV columns, found 2", exception.getMessage());
    }

    @Test
    void rejectsBadCommitCount() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EcoreModelCsv.parseLine("models.ecore,owner/repo,https://github.com/owner/repo,url,nope", 3));

        assertEquals("Line 3 commit count is not a number: nope", exception.getMessage());
    }

    @Test
    void rejectsMalformedRepositoryName() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EcoreModelCsv.parseLine("models.ecore,owner,https://github.com/owner,url,1", 4));

        assertEquals("Line 4 has malformed repository full name: owner", exception.getMessage());
    }
}
