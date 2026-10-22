package com.fadihania;

import java.nio.file.Path;

public final class App {
    private static final Path DEFAULT_INPUT = Path.of("models", "ecore-model.txt");
    private static final Path DEFAULT_OUTPUT = Path.of("downloads");

    private App() {
    }

    public static void main(String[] args) {
        int exitCode = run(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    static int run(String[] args) {
        try {
            CliOptions options = CliOptions.parse(args);
            EcoreHistoryDownloader downloader = new EcoreHistoryDownloader(
                    options.inputPath(),
                    options.outputPath(),
                    GitHubConnector.fromEnvironment());
            DownloadSummary summary = downloader.downloadAll();
            System.out.printf("Finished: %d rows processed, %d versions downloaded, %d failed rows.%n",
                    summary.rowsProcessed(), summary.versionsDownloaded(), summary.failedRows());
            return summary.failedRows() == 0 ? 0 : 1;
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
            return 2;
        } catch (Exception exception) {
            System.err.println("Download failed: " + exception.getMessage());
            return 1;
        }
    }

    record CliOptions(Path inputPath, Path outputPath) {
        static CliOptions parse(String[] args) {
            Path inputPath = DEFAULT_INPUT;
            Path outputPath = DEFAULT_OUTPUT;

            for (int index = 0; index < args.length; index++) {
                String arg = args[index];
                switch (arg) {
                    case "--input" -> {
                        index = requireValue(args, index, arg);
                        inputPath = Path.of(args[index]);
                    }
                    case "--output" -> {
                        index = requireValue(args, index, arg);
                        outputPath = Path.of(args[index]);
                    }
                    case "--help", "-h" -> throw new IllegalArgumentException(usage());
                    default -> throw new IllegalArgumentException("Unknown argument: " + arg + System.lineSeparator() + usage());
                }
            }

            return new CliOptions(inputPath, outputPath);
        }

        private static int requireValue(String[] args, int index, String option) {
            int valueIndex = index + 1;
            if (valueIndex >= args.length || args[valueIndex].startsWith("--")) {
                throw new IllegalArgumentException("Missing value for " + option + System.lineSeparator() + usage());
            }
            return valueIndex;
        }

        private static String usage() {
            return "Usage: mvn exec:java -Dexec.args=\"[--input models/ecore-model.txt] [--output downloads]\"";
        }
    }
}
