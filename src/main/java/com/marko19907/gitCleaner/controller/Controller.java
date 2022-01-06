package com.marko19907.gitCleaner.controller;

import com.marko19907.gitCleaner.model.GitCleaner;
import com.marko19907.gitCleaner.utilities.CleaningInterruptedException;
import com.marko19907.gitCleaner.view.Dialogs;
import com.marko19907.gitCleaner.view.ProgressDialog;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.Objects;

/**
 * The main controller.
 */
public class Controller {

    /**
     * The selected folder in the UI.
     */
    private File selectedDirectory;

    /**
     * The logic.
     */
    private GitCleaner cleanerLogic;

    /**
     * Default no-arg constructor.
     */
    public Controller() {
        this.selectedDirectory = null;
        this.cleanerLogic = null;
    }

    /**
     * Responds to the browse action event.
     */
    public void doBrowseAction(TextField pathField, TextArea gitFoldersTextArea, Button cleanButton) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(this.selectedDirectory);
        File directory = directoryChooser.showDialog(null);

        if (directory == null) {
            this.resetTextFields(pathField, gitFoldersTextArea);
            cleanButton.setDisable(true);
        }
        else {
            this.analyzeSelectedFolder(directory, pathField, gitFoldersTextArea, cleanButton);
        }
    }

    /**
     * Resets the two given TextFields to their default values.
     */
    private void resetTextFields(TextField pathField, TextArea gitFoldersTextArea) {
        Objects.requireNonNull(pathField);
        Objects.requireNonNull(gitFoldersTextArea);

        pathField.setText("No folder selected");
        gitFoldersTextArea.clear();
    }

    /**
     * Analyzes the selected folder in a separate Thread.
     * @param directory          The File to analyze
     * @param pathField          The path TextField to update with the directory path
     * @param gitFoldersTextArea The TextArea to post the results to
     * @param cleanButton        The clean button to disable/enable
     */
    private void analyzeSelectedFolder(File directory,
                                       TextField pathField,
                                       TextArea gitFoldersTextArea,
                                       Button cleanButton) {
        this.selectedDirectory = directory;
        pathField.setText(this.selectedDirectory.getAbsolutePath());


        ProgressDialog dialog = new ProgressDialog();
        dialog.setTitle("Analyzing");
        dialog.setHeaderText("Analyzing the selected folder");
        dialog.setContentText("Searching for Git projects in the selected folder, please wait . . .");
        dialog.setInfinite();

        // Long analysis task
        Task<Void> longTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Controller.this.cleanerLogic = new GitCleaner(Controller.this.selectedDirectory);
                gitFoldersTextArea.setText(Controller.this.cleanerLogic.getGitFoldersString());

                //this.longTaskSimulation();

                return null;
            }

            /**
             * A long loop, for testing only.
             * @throws InterruptedException If any thread interrupts the current thread
             */
            private void longTaskSimulation() throws InterruptedException {
                int max = 100;
                for (int i = 1; i <= max; i++) {
                    if (this.isCancelled()) {
                        break;
                    }
                    this.updateProgress(i, max);
                    this.updateMessage("Task part " + i + " complete");
                    System.out.println("Task part " + i + " complete");

                    Thread.sleep(100);
                }
            }
        };

        longTask.setOnSucceeded(e -> {
            cleanButton.setDisable(this.cleanerLogic == null || this.cleanerLogic.getFoundFolderNumber() == 0);
            dialog.hide();
        });

        longTask.setOnCancelled(e -> {
            dialog.hide();
            Dialogs.showAnalysisInterruptedDialog("Folder analysis cancelled by user . . .");

            this.resetTextFields(pathField, gitFoldersTextArea);
            this.selectedDirectory = null;
            this.cleanerLogic = null;
        });

        dialog.setOnCloseRequest(e -> longTask.cancel());

        dialog.show();

        Thread thread = new Thread(longTask);
        thread.start();
    }

    /**
     * Responds to the clean action event.
     */
    public void doCleanAction() {
        if (this.selectedDirectory == null) {
            // No directory has been selected yet
            Dialogs.showNoSelectionDialog();
        }
        else {
            this.runCleaner();
        }
    }

    /**
     * Runs the cleaner logic, assumes that a folder is already selected.
     * @throws NullPointerException If the folder is null (not selected) or if the logic is null
     */
    private void runCleaner() {
        Objects.requireNonNull(this.cleanerLogic);
        Objects.requireNonNull(this.selectedDirectory);

        ProgressDialog dialog = new ProgressDialog();
        dialog.setTitle("Cleaning");
        dialog.setHeaderText("Cleaning the selected folder");
        dialog.setContentText("Cleaning all Git projects in the selected folder, please wait . . .");

        // Long cleanup task
        Task<Void> longTask = new Task<>() {
            @Override
            protected Void call() {
                Controller.this.cleanerLogic.setProgress((workDone, max) -> {
                    this.updateProgress(workDone, max);
                    dialog.setDialogProgress((double) workDone / max);
                });

                try {
                    Controller.this.cleanerLogic.run();
                }
                catch (CleaningInterruptedException e) {
                    // The cleaning process was interrupted,
                    // no need to do anything here as it is handled by the setOnCancelled() event
                }

                return null;
            }
        };

        longTask.setOnSucceeded(e -> {
            dialog.hide();
            Dialogs.showCleaningSuccessfulDialog(this.cleanerLogic.getCleaningReport());
        });
        longTask.setOnCancelled(e -> {
            dialog.hide();
            Dialogs.showCleaningInterruptedDialog("Cleaning cancelled by user . . .");
        });

        dialog.setOnCloseRequest(e -> longTask.cancel());
        dialog.show();

        Thread thread = new Thread(longTask);
        thread.start();
    }
}
