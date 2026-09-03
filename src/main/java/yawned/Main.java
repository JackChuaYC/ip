package yawned;

import java.nio.file.Path;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Starts the Yawned JavaFX application.
 */
public class Main extends Application {
    private static final Path DEFAULT_FILE_PATH = Path.of("data", "Yawned.txt");

    @Override
    public void start(Stage stage) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.setYawned(new Yawned(DEFAULT_FILE_PATH));

        stage.setTitle("Yawned");
        stage.setResizable(false);
        stage.setScene(new Scene(mainWindow));
        stage.show();
    }
}
