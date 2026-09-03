package yawned;

import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts the Yawned JavaFX application.
 */
public class Main extends Application {
    private final Yawned yawned = new Yawned(Path.of("data", "Yawned.txt"));

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setYawned(yawned);

            stage.setScene(new Scene(anchorPane));
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the main window layout.", exception);
        }
    }
}
