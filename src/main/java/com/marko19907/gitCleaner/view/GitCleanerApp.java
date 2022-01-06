package com.marko19907.gitCleaner.view;

import com.marko19907.gitCleaner.controller.Controller;
import com.marko19907.gitCleaner.utilities.Utilities;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Class GitCleanerApp represents the main window in the application.
 */
public class GitCleanerApp extends Application {

    /**
     * The clean button width.
     */
    private static final int CLEAN_BUTTON_WIDTH = 70;

    /**
     * The main controller.
     */
    private final Controller controller;

    /**
     * The root BorderPane.
     */
    private final BorderPane root;

    /**
     * The path TextFiled, used to display the currently selected path.
     */
    private final TextField pathField;

    /**
     * The git folders TextArea, used to display the path to all git projects found in the selected path.
     */
    private final TextArea gitFoldersTextArea;

    /**
     * The clean button.
     */
    private final Button cleanButton;

    /**
     * The main method.
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * GitCleanerApp constructor.
     */
    public GitCleanerApp() {
        this.controller = new Controller();

        this.root = new BorderPane();
        this.pathField = new TextField();
        this.gitFoldersTextArea = new TextArea();
        this.cleanButton = new Button("Clean");
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Git Cleaner");
        primaryStage.getIcons().add(Utilities.getIcon());
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);

        Node center = this.buildCenter();
        BorderPane.setMargin(center, new Insets(10));
        this.root.setCenter(center);

        Scene scene = new Scene(this.root, 400, 300, Color.WHITE);
        primaryStage.setScene(scene);
        primaryStage.show();

        this.root.requestFocus();
    }

    /**
     * Builds the center VBox.
     */
    private VBox buildCenter() {
        VBox vBox = new VBox();
        vBox.setSpacing(2);

        Text text = new Text("Select a folder");
        text.setStyle("-fx-font: 14 arial;");

        HBox browseBox = this.buildBrowseBox();

        Region spacer = new Region();
        spacer.setMinHeight(10);

        this.cleanButton.setDisable(true);
        this.cleanButton.setOnAction(e -> this.controller.doCleanAction());
        this.cleanButton.setMinWidth(CLEAN_BUTTON_WIDTH);

        this.gitFoldersTextArea.setEditable(false);
        VBox.setVgrow(this.gitFoldersTextArea, Priority.ALWAYS);

        vBox.getChildren().addAll(text, browseBox, spacer, this.cleanButton, this.gitFoldersTextArea);
        return vBox;
    }

    /**
     * Builds the HBox for the path TextField and the browse Button.
     */
    private HBox buildBrowseBox() {
        HBox hBox = new HBox();
        hBox.setSpacing(2);

        this.pathField.setEditable(false);
        HBox.setHgrow(this.pathField, Priority.ALWAYS);

        Button browseButton = new Button("Browse");
        browseButton.setOnAction(e ->
                this.controller.doBrowseAction(this.pathField, this.gitFoldersTextArea, this.cleanButton)
        );

        hBox.getChildren().addAll(this.pathField, browseButton);
        return hBox;
    }
}
