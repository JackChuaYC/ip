package yawned;

import java.nio.file.Path;
import java.util.Scanner;

import yawned.exception.YawnedException;
import yawned.parser.CommandType;
import yawned.parser.Parser;
import yawned.storage.Storage;
import yawned.task.Task;
import yawned.task.TaskList;
import yawned.ui.Ui;
import yawned.gui.DialogBox;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.scene.image.Image;

/**
 * Coordinates the UI, command parser, task list, and storage for Yawned.
 */
public class Yawned extends Application {
    private final Ui ui;
    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private static final Path DEFAULT_FILE_PATH = Path.of("data", "Yanwed.txt");

    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image yawnedImage = new Image(this.getClass().getResourceAsStream("/images/DaYawned.png"));

    /**
     * Creates the chatbot and loads its saved tasks.
     *
     * @param saveFile Relative path of the task storage file.
     */
    public Yawned(Path saveFile) {
        ui = new Ui(new Scanner(System.in));
        parser = new Parser();
        storage = new Storage(saveFile);
        tasks = new TaskList(storage.loadTasks());
    }

    @Override
    public void start(Stage stage) {

        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);

        userInput = new TextField();
        sendButton = new Button("Send");

        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane,userInput, sendButton);

        stage.setTitle("Yawned");
        stage.setResizable(false);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);

        mainLayout.setPrefSize(400.0, 600.0);

        scrollPane.setPrefSize(385, 535);
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

        sendButton.setOnMouseClicked((event) -> {
            handleUserInput();
        });
        userInput.setOnAction((event) -> {
            handleUserInput();
        });

        dialogContainer.heightProperty().addListener((observable -> scrollPane.setVvalue(1.0)));
        scene = new Scene(mainLayout);

        stage.setScene(scene);
        stage.show();

    }

    /**
     * Creates a dialog box containing user input, and appends it to
     * the dialog container. Clears the user input after processing.
     */
    public void handleUserInput() {
        dialogContainer.getChildren().addAll(new DialogBox(userInput.getText(), userImage));
        userInput.clear();
    }

    /**
     * Creates the JavaFX application with the default task storage path.
     */
    public Yawned() {
        this(DEFAULT_FILE_PATH);
    }


    /** Starts the interactive chatbot session. */
    public void run() {
        ui.showWelcome();
        String userInput = ui.readCommand("*Yawns..* You woke me up...\nWhat do you want?\n");
        ui.showBreakLine();
        CommandType commandType = parser.parseCommandType(userInput);
        while (commandType != CommandType.BYE) {
            switch (commandType) {
                case LIST:
                    ui.showTaskList(tasks);
                    userInput = ui.readCommand("");
                    break;
                case MARK:
                    userInput = ui.readCommand(markTask(userInput));
                    break;
                case UNMARK:
                    userInput = ui.readCommand(unmarkTask(userInput));
                    break;
                case DELETE:
                    userInput = ui.readCommand(deleteTaskMessage(userInput));
                    break;
                case FIND:
                    userInput = ui.readCommand(findTaskMessage(userInput));
                    break;
                case TODO:
                case DEADLINE:
                case EVENT:
                case UNKNOWN:
                    try {
                        Task task = parser.parseTask(commandType, userInput);
                        addTask(task);
                        userInput = ui.readCommand(addedTaskMessage(task, tasks.size()));
                    } catch (YawnedException exception) {
                        userInput = ui.readCommand(exception.getMessage());
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected command type: " + commandType);
            }
            ui.showBreakLine();
            commandType = parser.parseCommandType(userInput);
        }
        ui.showMessage("Bye.. I am going back to sleep.");
        ui.showBreakLine();
    }

    /**
     * Adds a task and saves the changed task list.
     *
     * @param task New task to add.
     */
    private void addTask(Task task) {
        tasks.addTask(task);
        storage.saveTasks(tasks.getTasks());
    }

    /**
     * Removes a task and saves the changed task list.
     *
     * @param taskNumber One-based number of the task to remove.
     * @return Removed task.
     */
    private Task deleteTask(int taskNumber) {
        Task deletedTask = tasks.deleteTask(taskNumber);
        storage.saveTasks(tasks.getTasks());
        return deletedTask;
    }

    /**
     * Formats the confirmation shown after a task is successfully added.
     *
     * @param task Added task.
     * @param taskCounter Updated number of tasks.
     * @return Confirmation message.
     */
    private static String addedTaskMessage(Task task, int taskCounter) {
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + taskCounter + " tasks in the list.";
    }

    /**
     * Formats the confirmation shown after a task is deleted.
     *
     * @param task Deleted task.
     * @param taskCounter Updated number of tasks.
     * @return Confirmation message.
     */
    private static String deletedTaskMessage(Task task, int taskCounter) {
        return "fine. I removed this task:\n  " + task
                + "\nNow you have " + taskCounter + " tasks in the list.";
    }

    /**
     * Deletes the task selected by a {@code delete <number>} command and formats the result.
     *
     * @param command User command.
     * @return Deletion confirmation or validation message.
     */
    private String deleteTaskMessage(String command) {
        try {
            int taskNumber = parser.parseTaskNumber(CommandType.DELETE, command);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                return "you... don't have that task number???";
            }
            Task deletedTask = deleteTask(taskNumber);
            return deletedTaskMessage(deletedTask, tasks.size());
        } catch (YawnedException exception) {
            return exception.getMessage();
        }
    }

    /**
     * Marks the task selected by a {@code mark <number>} command as done.
     *
     * @param command User command.
     * @return Result message for the user.
     */
    private String markTask(String command) {
        try {
            int taskNumber = parser.parseTaskNumber(CommandType.MARK, command);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                return "you... don't have that task number???";
            }
            Task task = tasks.markTask(taskNumber);
            storage.saveTasks(tasks.getTasks());
            return "finally, that's done:\n  " + task;
        } catch (YawnedException exception) {
            return exception.getMessage();
        }
    }

    /**
     * Marks the task selected by an {@code unmark <number>} command as not done.
     *
     * @param command User command.
     * @return Result message for the user.
     */
    private String unmarkTask(String command) {
        try {
            int taskNumber = parser.parseTaskNumber(CommandType.UNMARK, command);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                return "you... don't have that task number???";
            }
            Task task = tasks.unmarkTask(taskNumber);
            storage.saveTasks(tasks.getTasks());
            return "As productive as me... unmarked:\n  " + task;
        } catch (YawnedException exception) {
            return exception.getMessage();
        }
    }

    /**
     * Finds tasks selected by a {@code find <keyword>} command and shows the results.
     *
     * @param command user command
     * @return prompt message for the next command
     */
    private String findTaskMessage(String command) {
        try {
            ui.showMatchingTasks(tasks.findTasks(parser.parseFindKeyword(command)));
            return "";
        } catch (YawnedException exception) {
            return exception.getMessage();
        }
    }

    /** Starts Yawned using its standard relative storage path. */
    public static void main(String[] args) {
        new Yawned(Path.of("data", "Yawned.txt")).run();
    }
}
