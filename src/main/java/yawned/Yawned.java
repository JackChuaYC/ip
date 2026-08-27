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

/**
 * Coordinates the UI, command parser, task list, and storage for Yawned.
 */
public class Yawned {
    private final Ui ui;
    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Creates the chatbot and loads its saved tasks.
     *
     * @param saveFile relative path of the task storage file
     */
    public Yawned(Path saveFile) {
        ui = new Ui(new Scanner(System.in));
        parser = new Parser();
        storage = new Storage(saveFile);
        tasks = new TaskList(storage.loadTasks());
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
     * @param task new task to add
     */
    private void addTask(Task task) {
        tasks.addTask(task);
        storage.saveTasks(tasks.getTasks());
    }

    /**
     * Removes a task and saves the changed task list.
     *
     * @param taskNumber one-based number of the task to remove
     * @return removed task
     */
    private Task deleteTask(int taskNumber) {
        Task deletedTask = tasks.deleteTask(taskNumber);
        storage.saveTasks(tasks.getTasks());
        return deletedTask;
    }

    /**
     * Formats the confirmation shown after a task is successfully added.
     *
     * @param task added task
     * @param taskCounter updated number of tasks
     * @return confirmation message
     */
    private static String addedTaskMessage(Task task, int taskCounter) {
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + taskCounter + " tasks in the list.";
    }

    /**
     * Formats the confirmation shown after a task is deleted.
     *
     * @param task deleted task
     * @param taskCounter updated number of tasks
     * @return confirmation message
     */
    private static String deletedTaskMessage(Task task, int taskCounter) {
        return "fine. I removed this task:\n  " + task
                + "\nNow you have " + taskCounter + " tasks in the list.";
    }

    /**
     * Deletes the task selected by a {@code delete <number>} command and formats the result.
     *
     * @param command user command
     * @return deletion confirmation or validation message
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
     * @param command user command
     * @return result message for the user
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
     * @param command user command
     * @return result message for the user
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
