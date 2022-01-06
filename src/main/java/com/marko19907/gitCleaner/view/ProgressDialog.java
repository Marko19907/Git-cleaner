package com.marko19907.gitCleaner.view;

import com.marko19907.gitCleaner.utilities.Utilities;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Displays a dialog window with a progress bar.
 */
public class ProgressDialog extends Dialog<Boolean> {
    private final VBox vbox;
    private final Label label;
    private final ProgressBar progressBar;
    private final boolean isFinished = false;

    /**
     * Creates a new progress dialog at 0% progress.
     */
    public ProgressDialog() {
        this(0.0d);
    }

    /**
     * Creates a new progress dialog with the given progress.
     * @param progress The progress to apply to the progress bar. Must be between 0 and 1.
     */
    public ProgressDialog(double progress) {
        final DialogPane dialogPane = this.getDialogPane();
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(Utilities.getIcon());

        this.vbox = new VBox();
        this.vbox.setFillWidth(true);
        this.vbox.setMaxWidth(Double.MAX_VALUE);
        this.vbox.setAlignment(Pos.CENTER_LEFT);

        this.progressBar = new ProgressBar(progress);
        this.progressBar.prefWidthProperty().bind(this.vbox.widthProperty().subtract(10));

        this.label = createContentLabel(dialogPane.getContentText());
        this.label.setPrefWidth(Region.USE_COMPUTED_SIZE);
        this.label.textProperty().bind(dialogPane.contentTextProperty());

        dialogPane.contentTextProperty().addListener(o -> this.updateGrid());

        this.setTitle("Please Wait");
        dialogPane.setHeaderText("Loading...");
        dialogPane.getStyleClass().add("text-input-dialog");
        dialogPane.getButtonTypes().add(ButtonType.CANCEL);
        this.updateGrid();

        this.setResult(this.isFinished);
    }

    /**
     * Performs the function of <code>DialogPane.createContentLabel</code>. The
     * built-in JavaFX dialogs call that method, but this class cannot access
     * it. This method is used by this class instead.
     * @param text The text to be applied to the label.
     * @return A new label for the dialog.
     */
    private static Label createContentLabel(String text) {
        Label label = new Label(text);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        label.getStyleClass().add("content");
        label.setWrapText(true);
        label.setPrefWidth(360);
        return label;
    }

    /**
     * Set the current state of the progress bar. This method accepts a double
     * from 0-1 and fills in the progress bar accordingly.
     * @param progress The progress to apply to the progress bar. Must be between 0 and 1.
     */
    public void setDialogProgress(double progress) {
        this.progressBar.setProgress(progress);
    }

    /**
     * Refreshes the grid to display the current components.
     */
    private void updateGrid() {
        this.vbox.getChildren().clear();

        this.vbox.getChildren().add(this.progressBar);
        this.vbox.getChildren().add(this.label);
        this.getDialogPane().setContent(this.vbox);

        Platform.runLater(this.progressBar::requestFocus);
    }

    /**
     * Turns the dialog into an infinite progress dialog.
     */
    public void setInfinite() {
        this.progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
    }
}
