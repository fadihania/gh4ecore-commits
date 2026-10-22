package com.fadihania;

record DownloadSummary(int rowsProcessed, int versionsDownloaded, int failedRows) {
    DownloadSummary add(DownloadSummary other) {
        return new DownloadSummary(
                rowsProcessed + other.rowsProcessed,
                versionsDownloaded + other.versionsDownloaded,
                failedRows + other.failedRows);
    }

    static DownloadSummary rowSuccess(int versionsDownloaded) {
        return new DownloadSummary(1, versionsDownloaded, 0);
    }

    static DownloadSummary rowFailure() {
        return new DownloadSummary(1, 0, 1);
    }
}
