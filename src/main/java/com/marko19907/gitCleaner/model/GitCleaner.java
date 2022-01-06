package com.marko19907.gitCleaner.model;

import com.marko19907.gitCleaner.utilities.CleaningInterruptedException;
import com.marko19907.gitCleaner.utilities.Utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

/**
 * The model class, receives input from the controller and manages the data.
 */
public class GitCleaner {

    /**
     * The Git clean command.
     */
    private static final String GIT_CLEAN_COMMAND = "git gc --aggressive --prune";

    /**
     * The previous size.
     */
    private final long sizeBefore;

    /**
     * The previous size of all .git folders.
     */
    private final long gitFoldersSizeBefore;

    /**
     * The input directory to clean.
     */
    private final File inputDirectory;

    /**
     * A BiConsumer, used to update the progress during the cleaning process.
     */
    private BiConsumer<Integer, Integer> progressUpdate;

    /**
     * The Set of found git folders.
     */
    private Set<File> gitFolders;

    /**
     * Constructor for GitCleaner objects.
     * @param file The directory (File) to start from
     * @throws IllegalArgumentException      If the given File is null
     * @throws UnsupportedOperationException If the given File is not a directory
     */
    public GitCleaner(File file) {
        this.checkFile(file);

        this.sizeBefore = this.getFolderSize(file);
        this.gitFoldersSizeBefore = this.getGitFoldersSize(file);
        this.inputDirectory = file;
        this.gitFolders = null;
    }

    /**
     * Runs the cleaning procedure on the selected Set of folders.
     */
    public void run() {
        final Set<File> folders = this.getGitFolders();

        // The Set must be converted to a List to enable progress tracking
        final List<File> folderList = new ArrayList<>(folders);

        IntStream.range(0, folderList.size())
                .forEachOrdered(index -> {
                    final File currentFolder = folderList.get(index);

                    if (currentFolder != null) {
                        try {
                            String command = "cd " + currentFolder.getAbsolutePath() + " & " + GIT_CLEAN_COMMAND;

                            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
                            Process process = processBuilder.start();

                            process.waitFor();

                            if (this.progressUpdate != null) {
                                this.progressUpdate.accept(index, folderList.size());
                            }
                            else {
                                // The UI is not running, print to console
                                System.out.println("Progress: " + index + "/" + folderList.size());
                            }
                        }

                        catch (IOException | InterruptedException e) {
                            throw new CleaningInterruptedException(e.getMessage());
                        }
                    }
                });

        if (this.progressUpdate != null) {
            this.progressUpdate.accept(1, 1);
        }
        else {
            // The UI is not running, print to console
            System.out.println("Cleaning done!");
            System.out.println();
            System.out.println("Cleaning report: ");
            System.out.println(this.getCleaningReport());
        }
    }

    /**
     * Sets the progress.
     */
    public void setProgress(BiConsumer<Integer, Integer> progressUpdate) {
        this.progressUpdate = progressUpdate;
    }

    /**
     * Returns the cleaning report as a single String.
     */
    public String getCleaningReport() {
        final StringBuilder builder = new StringBuilder();
        final long sizeAfter = this.getFolderSize(this.inputDirectory);
        final long gitFolderSizeAfter = this.getGitFoldersSize(this.inputDirectory);

        builder.append("Size before: ").append(Utilities.formatSize(this.sizeBefore));
        builder.append("\n");
        builder.append("Size after: ").append(Utilities.formatSize(sizeAfter));
        builder.append("\n");
        builder.append("Delta: ").append(Utilities.formatSize(this.sizeBefore - sizeAfter));
        builder.append("\n");
        builder.append("\n");
        builder.append("Size of all .git folders before: ").append(Utilities.formatSize(this.gitFoldersSizeBefore));
        builder.append("\n");
        builder.append("Size of all .git folders after: ").append(Utilities.formatSize(gitFolderSizeAfter));
        builder.append("\n");
        builder.append("Delta: ").append(Utilities.formatSize(this.gitFoldersSizeBefore - gitFolderSizeAfter));

        return builder.toString();
    }

    /**
     * Returns a Set of all git folders.
     */
    private Set<File> getGitFolders() {
        if (this.gitFolders == null) {
            GitFolderSearch searchAlgo = new GitFolderSearch();
            this.gitFolders = searchAlgo.getGitFolders(this.inputDirectory.getAbsolutePath());
        }
        return this.gitFolders;
    }

    /**
     * Returns the found git folders as a single String.
     */
    public String getGitFoldersString() {
        final Set<File> folders = this.getGitFolders();
        // Guard condition
        if (folders.isEmpty()) {
            return "No git folders found";
        }

        final StringBuilder builder = new StringBuilder();
        builder.append("Found ").append(this.getFoundFolderNumber()).append(" git ");
        builder.append((this.getFoundFolderNumber() > 1 ? "projects" : "project"));
        builder.append(" in the given folder");
        builder.append("\n");
        builder.append("\n");

        folders.forEach(folder -> {
            if (folder != null) {
                builder.append(folder.getAbsolutePath());
                builder.append("\n");
            }
        });

        return builder.toString();
    }

    /**
     * Returns the number of found git folders.
     */
    public int getFoundFolderNumber() {
        return this.getGitFolders().size();
    }

    /**
     * Checks if the given File is valid, must be a directory and not null.
     * @param file The File to check, not null
     * @throws IllegalArgumentException      If the given File is null
     * @throws UnsupportedOperationException If the given File is not a directory
     */
    private void checkFile(File file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("The given file can not be null!");
        }
        if (!file.isDirectory()) {
            throw new UnsupportedOperationException("The file must be a directory!");
        }
    }

    /**
     * Returns the length of the given directory in bytes.
     * @throws IllegalArgumentException If the given directory is null
     */
    private long getFolderSize(File file) {
        if (file == null) {
            throw new IllegalArgumentException("The given file can not be null!");
        }

        try {
            return Files.walk(file.toPath())
                    .filter(p -> p.toFile().isFile())
                    .mapToLong(p -> p.toFile().length())
                    .sum();
        }
        catch (IOException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    /**
     * Returns the length of all .git folders in the given directory in bytes.
     * @throws IllegalArgumentException If the given directory is null
     */
    private long getGitFoldersSize(File file) {
        if (file == null) {
            throw new IllegalArgumentException("The given file can not be null!");
        }

        try {
            return Files.walk(file.toPath())
                    .filter(p -> p.toFile().isDirectory() && p.toFile().getName().equals(".git"))
                    .mapToLong(p -> this.getFolderSize(p.toFile()))
                    .sum();
        }
        catch (IOException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    /**
     * The main method, for testing only.
     */
    public static void main(String[] args) {
        System.out.println(new GitCleaner(new File(".")).getFolderSize(new File(".")));

        GitCleaner gitCleaner = new GitCleaner(new File("Test directory"));
        gitCleaner.run();
    }
}
