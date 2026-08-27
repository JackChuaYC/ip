import java.nio.file.Path;
import java.util.Scanner;

/**
 * Entry point for the Yawned chatbot application.
 */
public class Yawned {
    public static void main(String[] args) {
        Ui ui = new Ui(new Scanner(System.in));
        Parser parser = new Parser();
        ui.showWelcome();
        Storage storage = new Storage(Path.of("data", "Yawned.txt"));
        TaskList listOfTasks = new TaskList(storage.loadTasks());
        String userInput = ui.readCommand("*Yawns..* You woke me up...\nWhat do you want?\n");
        ui.showBreakLine();
        CommandType commandType = parser.parseCommandType(userInput);
        while (commandType != CommandType.BYE) {
            switch (commandType) {
                case LIST:
                    ui.showTaskList(listOfTasks);
                    userInput = ui.readCommand("");
                    break;
                case MARK:
                    userInput = ui.readCommand(markTask(listOfTasks, storage, parser, userInput));
                    break;
                case UNMARK:
                    userInput = ui.readCommand(unmarkTask(listOfTasks, storage, parser, userInput));
                    break;
                case DELETE:
                    userInput = ui.readCommand(deleteTaskMessage(listOfTasks, storage, parser, userInput));
                    break;
                case TODO:
                case DEADLINE:
                case EVENT:
                case UNKNOWN:
                    try {
                        Task task = parser.parseTask(commandType, userInput);
                        addTask(listOfTasks, storage, task);
                        userInput = ui.readCommand(addedTaskMessage(task, listOfTasks.size()));
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
     * Adds a task to the list of tasks
     * @param listOfTasks task list
     * @param task new task to be added
     */
    private static void addTask(TaskList listOfTasks, Storage storage, Task task) {
        listOfTasks.addTask(task);
        storage.saveTasks(listOfTasks.getTasks());
    }

    /**
     * Removes a task while keeping the remaining task numbers contiguous.
     *
     * @param listOfTasks task list
     * @param taskNumber one-based number of the task to remove
     * @return removed task
     */
    private static Task deleteTask(TaskList listOfTasks, Storage storage, int taskNumber) {
        Task deletedTask = listOfTasks.deleteTask(taskNumber);
        storage.saveTasks(listOfTasks.getTasks());
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
     * @param listOfTasks task list
     * @param storage task storage
     * @param parser command parser
     * @param command user command
     * @return deletion confirmation or validation message
     */
    private static String deleteTaskMessage(TaskList listOfTasks, Storage storage, Parser parser, String command) {
        try {
            int taskNumber = parser.parseTaskNumber(CommandType.DELETE, command);
            if (taskNumber < 1 || taskNumber > listOfTasks.size()) {
                return "you... don't have that task number???";
            }
            Task deletedTask = deleteTask(listOfTasks, storage, taskNumber);
            return deletedTaskMessage(deletedTask, listOfTasks.size());
        } catch (YawnedException exception) {
            return exception.getMessage();
        }
    }

    /**
     * Marks the task selected by a {@code mark <number>} command as done.
     *
     * @param listOfTasks task list
     * @param storage task storage
     * @param parser command parser
     * @param command user command
     * @return result message for the user
     */
    private static String markTask(TaskList listOfTasks, Storage storage, Parser parser, String command) {
        try {
            int taskNumber = parser.parseTaskNumber(CommandType.MARK, command);
            if (taskNumber < 1 || taskNumber > listOfTasks.size()) {
                return "you... don't have that task number???";
            }
            Task task = listOfTasks.getTask(taskNumber);
            task.markAsDone();
            storage.saveTasks(listOfTasks.getTasks());
            return "finally, that's done:\n  " + task;
        } catch (YawnedException exception) {
            return exception.getMessage();
        }
    }

    /**
     * Marks the task selected by an {@code unmark <number>} command as not done.
     *
     * @param listOfTasks task list
     * @param storage task storage
     * @param parser command parser
     * @param command user command
     * @return result message for the user
     */
    private static String unmarkTask(TaskList listOfTasks, Storage storage, Parser parser, String command) {
        try {
            int taskNumber = parser.parseTaskNumber(CommandType.UNMARK, command);
            if (taskNumber < 1 || taskNumber > listOfTasks.size()) {
                return "you... don't have that task number???";
            }
            Task task = listOfTasks.getTask(taskNumber);
            task.markAsUndone();
            storage.saveTasks(listOfTasks.getTasks());
            return "As productive as me... unmarked:\n  " + task;
        } catch (YawnedException exception) {
            return exception.getMessage();
        }
    }
}
