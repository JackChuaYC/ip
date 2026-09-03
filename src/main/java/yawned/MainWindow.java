package yawned;

import javafx.fxml.FXML;
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
