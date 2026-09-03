package yawned;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import yawned.gui.DialogBox;

/**
 * Controls the main Yawned graphical user interface.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private final Image userImage = new Image(getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image yawnedImage = new Image(getClass().getResourceAsStream("/images/DaYawned.png"));
    private Yawned yawned;

    /**
     * Creates the main window from its FXML layout.
     *
     * @throws IllegalStateException If the FXML layout cannot be loaded.
     */
    public MainWindow() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainWindow.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the main window layout.", exception);
        }
    }

    /**
     * Initializes the controls after FXML has injected them.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Sets the chatbot that supplies responses to user messages.
     *
     * @param yawned Chatbot instance to use for responses.
     */
    public void setYawned(Yawned yawned) {
        this.yawned = yawned;
    }

    /**
     * Appends the user's input and Yawned's response to the dialog container.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = yawned.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getYawnedDialog(response, yawnedImage));
        userInput.clear();
    }
}
