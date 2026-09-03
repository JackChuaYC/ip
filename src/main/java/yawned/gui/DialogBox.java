package yawned.gui;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Displays a message beside its sender's profile image.
 */
public class DialogBox extends HBox {
    private Label text;
    private ImageView displayPicture;

    /**
     * Creates a dialog box containing a message and its sender's image.
     *
     * @param text Message to display.
     * @param image Image representing the message sender.
     */
    public DialogBox(String s, Image i) {
        text = new Label(s);
        displayPicture = new ImageView(i);

        //Styling the dialog box
        text.setWrapText(true);
        displayPicture.setFitWidth(100.0);
        displayPicture.setFitHeight(100.0);
        this.setAlignment(Pos.TOP_RIGHT);

        this.getChildren().addAll(text, displayPicture);
    }

}
