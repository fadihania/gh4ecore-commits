package com.fadihania;

import java.util.List;

record RepoMetadata(String repoName, String url, List<ModelFileMetadata> modelFiles) {
}
