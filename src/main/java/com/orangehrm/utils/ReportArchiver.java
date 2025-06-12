package com.orangehrm.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ReportArchiver {

    public void archiveAllureResults() {
        String sourceDirPath = "allure-results";
        File sourceDir = new File(sourceDirPath);

        if (!sourceDir.exists() || sourceDir.listFiles() == null || sourceDir.listFiles().length == 0) {
            System.out.println("No existing Allure results to archive.");
            return;
        }

        // Wait until files are unlocked and stable
        waitUntilFilesAreUnlocked(sourceDir);

        // Create timestamped folder path
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        File versionedDir = new File("AllureArchives", "Report_" + timestamp);

        // Ensure parent folder exists
        if (!versionedDir.getParentFile().exists()) {
            synchronized (this) {
                if (!versionedDir.getParentFile().exists()) {
                    boolean created = versionedDir.getParentFile().mkdirs();
                    if (!created) {
                        System.err.println("Failed to create AllureArchives directory.");
                        return;
                    }
                }
            }
        }

        // Retry mechanism to handle locked files
        int attempts = 5;
        boolean copied = false;
        while (attempts > 0) {
            try {


                FileUtils.copyDirectory(sourceDir, versionedDir);
                System.out.println("Archived Allure results to: " + versionedDir.getAbsolutePath());
                copied = true;
                break;  // Exit the loop if copy is successful
            } catch (IOException e) {
                attempts--;

                if (attempts == 0) {
                    System.err.println("Max retry attempts reached. Archiving failed.");
                    break;
                }
                // Wait before retrying (e.g., 5 seconds)
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        if (copied) {
            // Optional: delete the old archive folders
            cleanOldArchives(new File("AllureArchives"), 7); // Keep 7 days of history
        } else {
            System.err.println("Allure results archiving was unsuccessful.");
        }
    }

    private void waitUntilFilesAreUnlocked(File dir) {
        System.out.println("Waiting for allure-results directory to be stable...");
        int maxWaitSeconds = 30;
        int waited = 0;

        while (waited < maxWaitSeconds) {
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) return;

            boolean allUnlocked = true;
            for (File file : files) {
                if (!isFileUnlocked(file)) {
                    allUnlocked = false;
                    break;
                }
            }

            if (allUnlocked) {
                System.out.println("Allure result directory is stable. Proceeding with archive...");
                return;
            }

            try {
                Thread.sleep(1000);
                waited++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        System.out.println("Timeout: allure-results directory might still be in use.");
    }

    private boolean isFileUnlocked(File file) {
        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            // Try writing a byte at end without altering content
            fos.getChannel().position(fos.getChannel().size());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void cleanOldArchives(File archiveBaseDir, int daysToKeep) {
        if (!archiveBaseDir.exists()) {
            System.out.println("Archive base directory does not exist.");
            return;
        }

        File[] archiveDirs = archiveBaseDir.listFiles();
        if (archiveDirs == null) return;

        long now = System.currentTimeMillis();

        for (File dir : archiveDirs) {
            if (dir.isDirectory()) {
                long ageInMillis = now - dir.lastModified();
                long ageInDays = TimeUnit.MILLISECONDS.toDays(ageInMillis);

                if (ageInDays > daysToKeep) {
                    try {
                        FileUtils.deleteDirectory(dir);
                        System.out.println("Deleted old archive: " + dir.getName());
                    } catch (IOException e) {
                        System.out.println("Failed to delete archive: " + dir.getName());
                    }
                }
            }
        }
    }
}
