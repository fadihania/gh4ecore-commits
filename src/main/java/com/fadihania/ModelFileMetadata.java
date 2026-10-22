package com.fadihania;

import java.util.List;

record ModelFileMetadata(String fileName, int commits, List<VersionMetadata> versions) {
}
