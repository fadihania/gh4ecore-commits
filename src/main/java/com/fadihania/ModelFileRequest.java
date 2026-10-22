package com.fadihania;

record ModelFileRequest(
        String modelFileName,
        String repositoryFullName,
        String repositoryUrl,
        String fileUrl,
        int minimumCommitCount) {
}
