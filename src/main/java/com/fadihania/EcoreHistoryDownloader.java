package com.fadihania;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class EcoreHistoryDownloader {
    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd")
            .withZone(ZoneOffset.UTC);

    private final Path inputPath;
    private final Path outputPath;
    private final GitHub gitHub;

    EcoreHistoryDownloader(Path inputPath, Path outputPath, GitHub gitHub) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.gitHub = gitHub;
    }

    DownloadSummary downloadAll() throws IOException {
        List<ModelFileRequest> requests = EcoreModelCsv.read(inputPath);
        Map<String, RepoMetadata> metadataByRepo = new LinkedHashMap<>();
        DownloadSummary summary = new DownloadSummary(0, 0, 0);

        Files.createDirectories(outputPath);

        for (ModelFileRequest request : requests) {
            try {
                int downloaded = downloadOne(request, metadataByRepo);
                summary = summary.add(DownloadSummary.rowSuccess(downloaded));
            } catch (Exception exception) {
                summary = summary.add(DownloadSummary.rowFailure());
                System.err.printf("Failed row for %s in %s: %s%n",
                        request.modelFileName(), request.repositoryFullName(), exception.getMessage());
            }
        }

        for (RepoMetadata metadata : metadataByRepo.values()) {
            Path repoDirectory = repositoryOutputDirectory(metadata.repoName());
            MetadataJsonWriter.write(repoDirectory.resolve("metadata.json"), metadata);
        }

        return summary;
    }

    private int downloadOne(ModelFileRequest request, Map<String, RepoMetadata> metadataByRepo) throws IOException {
        String filePath = GitHubPathResolver.repositoryPath(request.fileUrl(), request.repositoryFullName());
        Path repoDirectory = repositoryOutputDirectory(request.repositoryFullName());
        Files.createDirectories(repoDirectory);

        GHRepository repository = gitHub.getRepository(request.repositoryFullName());
        List<VersionMetadata> versions = new ArrayList<>();

        System.out.printf("Downloading %s from %s (%s)%n",
                request.modelFileName(), request.repositoryFullName(), filePath);

        for (GHCommit commit : repository.queryCommits().path(filePath).pageSize(100).list()) {
            String sha = commit.getSHA1();
            try {
                GHContent content = repository.getFileContent(filePath, sha);
                Path targetPath = repoDirectory.resolve(versionFileName(request.modelFileName(), commit.getCommitDate(), sha));
                long size;

                // Large files can return "encoding: none" from the Contents API,
                // which GHContent.read() cannot decode. Stream the blob directly.
                try (InputStream inputStream = repository.readBlob(content.getSha())) {
                    size = Files.copy(inputStream, targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }

                versions.add(new VersionMetadata(
                        commit.getCommitDate().toString(),
                        rawContentUrl(request.repositoryFullName(), sha, filePath),
                        size));
                System.out.printf("  saved %s (%d bytes)%n", targetPath.getFileName(), size);
            } catch (GHFileNotFoundException exception) {
                System.err.printf("  skipped %s: file is not present at this commit.%n", sha);
            }
        }

        if (versions.size() < request.minimumCommitCount()) {
            System.err.printf("Incomplete history for %s in %s: expected at least %d commits, found %d.%n",
                    request.modelFileName(), request.repositoryFullName(), request.minimumCommitCount(), versions.size());
        }

        RepoMetadata repoMetadata = metadataByRepo.computeIfAbsent(
                request.repositoryFullName(),
                repo -> new RepoMetadata(repo, request.repositoryUrl(), new ArrayList<>()));
        repoMetadata.modelFiles().add(new ModelFileMetadata(request.modelFileName(), versions.size(), versions));

        return versions.size();
    }

    private Path repositoryOutputDirectory(String repositoryFullName) {
        return outputPath.resolve(repositoryFullName.replace('/', '-'));
    }

    static String versionFileName(String modelFileName, java.time.Instant commitDate, String sha) {
        String baseName = stripEcoreExtension(Path.of(modelFileName).getFileName().toString());
        String shortSha = sha.length() <= 7 ? sha : sha.substring(0, 7);
        return baseName + "_" + FILE_DATE_FORMATTER.format(commitDate) + "_" + shortSha + ".ecore";
    }

    static String rawContentUrl(String repositoryFullName, String sha, String filePath) {
        return "https://raw.githubusercontent.com/" + repositoryFullName + "/" + sha + "/" + filePath;
    }

    private static String stripEcoreExtension(String fileName) {
        if (fileName.toLowerCase().endsWith(".ecore")) {
            return fileName.substring(0, fileName.length() - ".ecore".length());
        }
        return fileName;
    }
}
