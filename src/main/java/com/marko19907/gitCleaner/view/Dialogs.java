package com.marko19907.gitCleaner.view;

import com.marko19907.gitCleaner.utilities.Utilities;
import javafx.scene.control.Alert;

/**
 * The Dialogs class contains all the dialogs of the application.
 */
public class Dialogs {

    /**
     * Private constructor.
     */
    private Dialogs() {
    }

    /**
     * Shows the no selection dialog, used to signal that no folder to clean was selected.
     */
    public static void showNoSelectionDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Utilities.addIconToDialog(alert);
        alert.setTitle("No selection");
        alert.setHeaderText("No selection was made");
        alert.setContentText("Click the browse button to select a folder to clean first");
        alert.showAndWait();
    }

    /**
     * Shows the cleaning interrupted dialog.
     * @param reason The interrupt reason
     */
    public static void showCleaningInterruptedDialog(String reason) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        Utilities.addIconToDialog(alert);
        alert.setTitle("Cleaning interrupted");
        alert.setHeaderText("The cleaning process was interrupted!");
        alert.setContentText("Reason: " + reason);
        alert.showAndWait();
    }

    /**
     * Shows the analysis interrupted dialog.
     * @param reason The interrupt reason
     */
    public static void showAnalysisInterruptedDialog(String reason) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        Utilities.addIconToDialog(alert);
        alert.setTitle("Analysis interrupted");
        alert.setHeaderText("The analysis process was interrupted!");
        alert.setContentText("Reason: " + reason);
        alert.showAndWait();
    }

    /**
     * Shows the cleaning completed dialog.
     * @param report The cleaning report to show
     */
    public static void showCleaningSuccessfulDialog(String report) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Utilities.addIconToDialog(alert);
        alert.setTitle("Completed");
        alert.setHeaderText("The cleaning process was completed!");
        alert.setContentText("Cleaning done!" + "\n" + "\n" + report);
        alert.showAndWait();
    }
}
