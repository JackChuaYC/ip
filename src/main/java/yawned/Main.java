package yawned;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import yawned.gui.DialogBox;

import java.nio.file.Path;

/**
 * Creates and displays the Yawned JavaFX user interface.
 */
public class Main extends Application {
    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Image userImage = new Image(getClass().getResourceAsStream("/images/DaUser.png"));
    private Image yawnedImage = new Image(getClass().getResourceAsStream("/images/DaYawned.png"));
    private Yawned yawned = new Yawned(Path.of("data", "Yawned.txt"));


    @Override
    public void start(Stage stage) {
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);

        userInput = new TextField();
        Button sendButton = new Button("Send");

        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);

        stage.setTitle("Yawned");
        stage.setResizable(false);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);

        mainLayout.setPrefSize(400.0, 600.0);

        scrollPane.setPrefSize(385.0, 535.0);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);

        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        userInput.setPrefWidth(325.0);
        sendButton.setPrefWidth(55.0);

        AnchorPane.setTopAnchor(scrollPane, 1.0);
        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);
        AnchorPane.setLeftAnchor(userInput, 1.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);

        sendButton.setOnMouseClicked(event -> handleUserInput());
        userInput.setOnAction(event -> handleUserInput());

        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
        Scene scene = new Scene(mainLayout);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Adds the entered user message to the dialog container and clears the input field.
     */
    private void handleUserInput() {
        String userText = userInput.getText();
        String yawnedText = yawned.getResponse(userInput.getText());
        dialogContainer.getChildren().addAll(
                new DialogBox(userText, userImage),
                new DialogBox(yawnedText, yawnedImage)
        );
        userInput.clear();
    }
}
