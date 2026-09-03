package yawned;

import java.nio.file.Path;
import java.util.List;
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
     * @param saveFile Relative path of the task storage file.
     */
    public Yawned(Path saveFile) {
        ui = new Ui(new Scanner(System.in));
        parser = new Parser();
        storage = new Storage(saveFile);
        tasks = new TaskList(storage.loadTasks());
    }

    /**
     * Processes a user command and returns the resulting response.
     *
     * @param input User command to process.
     * @return Response generated after processing the command.
     */
    public String getResponse(String input) {
        CommandType commandType = parser.parseCommandType(input);
        return switch (commandType) {
            case LIST -> taskListMessage();
            case MARK -> markTask(input);
            case UNMARK -> unmarkTask(input);
            case DELETE -> deleteTaskMessage(input);
            case FIND -> findTaskMessage(input);
            case TODO, DEADLINE, EVENT, UNKNOWN -> addTaskMessage(commandType, input);
            case BYE -> "Bye.. I am going back to sleep.";
        };
    }

    /** Starts the interactive chatbot session. */
    public void run() {
        ui.showWelcome();
        String userInput = ui.readCommand("*Yawns..* You woke me up...\nWhat do you want?\n");
        ui.showBreakLine();
        while (parser.parseCommandType(userInput) != CommandType.BYE) {
            userInput = ui.readCommand(getResponse(userInput));
            ui.showBreakLine();
        }
        ui.showMessage("Bye.. I am going back to sleep.");
        ui.showBreakLine();
    }

    /**
     * Creates and saves the task described by a task-creation command.
     *
     * @param commandType Type of task to create.
     * @param command Complete user command.
     * @return Confirmation message or validation error.
     */
    private String addTaskMessage(CommandType commandType, String command) {
        try {
            Task task = parser.parseTask(commandType, command);
            addTask(task);
            return addedTaskMessage(task, tasks.size());
        } catch (YawnedException exception) {
            return exception.getMessage();
        }
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
     * Formats the complete current task list.
     *
     * @return Task-list message.
     */
    private String taskListMessage() {
        if (tasks.isEmpty()) {
            return "No Tasks!";
        }
        return taskListMessage("Here you go, the tasks in your list:", tasks.getTasks());
    }

    /**
     * Formats a labeled list of tasks.
     *
     * @param heading Heading to display before the tasks.
     * @param taskItems Tasks to display.
     * @return Formatted task-list message.
     */
    private static String taskListMessage(String heading, List<Task> taskItems) {
        StringBuilder message = new StringBuilder(heading);
        for (int index = 0; index < taskItems.size(); index++) {
            message.append('\n')
                    .append(index + 1)
                    .append('.')
                    .append(taskItems.get(index));
        }
        return message.toString();
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
     * Finds tasks selected by a {@code find <keyword>} command and formats the results.
     *
     * @param command user command
     * @return Matching-task list or validation message.
     */
    private String findTaskMessage(String command) {
        try {
            List<Task> matchingTasks = tasks.findTasks(parser.parseFindKeyword(command));
            if (matchingTasks.isEmpty()) {
                return "No matching tasks!";
            }
            return taskListMessage("Here are the matching tasks in your list:", matchingTasks);
        } catch (YawnedException exception) {
            return exception.getMessage();
        }
    }

    /** Starts Yawned using its standard relative storage path. */
    public static void main(String[] args) {
        new Yawned(Path.of("data", "Yawned.txt")).run();
    }
}
