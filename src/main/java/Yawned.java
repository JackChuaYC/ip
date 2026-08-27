import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Entry point for the Yawned chatbot application.
 */
public class Yawned {
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);

    public static void main(String[] args) {
        Ui ui = new Ui(new Scanner(System.in));
        ui.showWelcome();
        Storage storage = new Storage(Path.of("data", "Yawned.txt"));
        TaskList listOfTasks = new TaskList(storage.loadTasks());
        String userInput = ui.readCommand("*Yawns..* You woke me up...\nWhat do you want?\n");
        ui.showBreakLine();
        CommandType commandType = CommandType.fromInput(userInput);
        while (commandType != CommandType.BYE) {
            switch (commandType) {
                case LIST:
                    ui.showTaskList(listOfTasks);
                    userInput = ui.readCommand("");
                    break;
                case MARK:
                    userInput = ui.readCommand(markTask(listOfTasks, storage, userInput));
                    break;
                case UNMARK:
                    userInput = ui.readCommand(unmarkTask(listOfTasks, storage, userInput));
                    break;
                case DELETE:
                    userInput = ui.readCommand(deleteTaskMessage(listOfTasks, storage, userInput));
                    break;
                case TODO:
                case DEADLINE:
                case EVENT:
                case UNKNOWN:
                    try {
                        Task task = createTask(commandType, userInput);
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
            commandType = CommandType.fromInput(userInput);
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
     * Parses a date written in ISO {@code yyyy-MM-dd} format.
     *
     * @param dateText date text to parse
     * @return parsed date
     * @throws YawnedException if the date is not a valid {@code yyyy-MM-dd} date
     */
    private static LocalDate parseDate(String dateText) throws YawnedException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new YawnedException("Please use a valid date in yyyy-MM-dd format.");
        }
    }

    /**
     * Parses a date and time written in {@code yyyy-MM-dd HHmm} format.
     *
     * @param dateTimeText date and time text to parse
     * @return parsed date and time
     * @throws YawnedException if the input is not a valid date and time
     */
    private static LocalDateTime parseDateTime(String dateTimeText) throws YawnedException {
        try {
            return LocalDateTime.parse(dateTimeText, INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new YawnedException("Please use a valid date and time in yyyy-MM-dd HHmm format.");
        }
    }

    /**
     * Creates the task described by a task-creation command.
     *
     * @param command user command
     * @return the created task
     * @throws YawnedException if the command is incomplete or unknown
     */
    private static Task createTask(CommandType commandType, String command) throws YawnedException {
        String details = command.substring(commandType.getWord().length()).trim();
        switch (commandType) {
            case TODO:
                if (details.isEmpty()) {
                    throw new YawnedException("hey!!! The description of a todo cannot be empty. *yawns*");
                }
                return new ToDo(details);
            case DEADLINE:
                int byIndex = details.indexOf(" /by");
                if (details.isEmpty() || details.startsWith("/by")) {
                    throw new YawnedException(
                            "I just want to sleep... you forgot to provide a description for the deadline");
                }
                if (byIndex < 0) {
                    throw new YawnedException("you woke me up for this? A deadline must include a /by time.");
                }
                String endDate = details.substring(byIndex + " /by".length()).trim();
                if (endDate.isEmpty()) {
                    throw new YawnedException("you woke me up for this? A deadline must include a /by time.");
                }
                return new Deadline(details.substring(0, byIndex).trim(), parseDateTime(endDate));
            case EVENT:
                int fromIndex = details.indexOf(" /from");
                if (details.isEmpty() || details.startsWith("/from") || details.startsWith("/to")) {
                    throw new YawnedException(
                            "I just want to sleep... you forgot to provide a description for the event");
                }
                if (fromIndex < 0) {
                    throw new YawnedException("Excuse me, An event must include /from and /to times.");
                }
                int toIndex = details.indexOf(" /to", fromIndex + " /from".length());
                if (toIndex < 0) {
                    throw new YawnedException("Excuse me, An event must include /from and /to times.");
                }
                String fromDate = details.substring(fromIndex + " /from".length(), toIndex).trim();
                String toDate = details.substring(toIndex + " /to".length()).trim();
                if (fromDate.isEmpty() || toDate.isEmpty()) {
                    throw new YawnedException("Excuse me, An event must include /from and /to times.");
                }
                return new Event(details.substring(0, fromIndex).trim(), parseDateTime(fromDate), parseDateTime(toDate));
            case UNKNOWN:
                throw new YawnedException("urmmm, but I don't know what that means?? >:-(");
            default:
                throw new IllegalArgumentException("Cannot create a task from command type: " + commandType);
        }
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
     * @param command user command
     * @return deletion confirmation or validation message
     */
    private static String deleteTaskMessage(TaskList listOfTasks, Storage storage, String command) {
        String taskNumberText = command.substring(CommandType.DELETE.getWord().length()).trim();
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > listOfTasks.size()) {
                return "you... don't have that task number???";
            }
            Task deletedTask = deleteTask(listOfTasks, storage, taskNumber);
            return deletedTaskMessage(deletedTask, listOfTasks.size());
        } catch (NumberFormatException exception) {
            return "*Yawns* You need to tell me which number to delete.. like: delete 2";
        }
    }

    /**
     * Marks the task selected by a {@code mark <number>} command as done.
     *
     * @param listOfTasks task list
     * @param command user command
     * @return result message for the user
     */
    private static String markTask(TaskList listOfTasks, Storage storage, String command) {
        String taskNumberText = command.substring(CommandType.MARK.getWord().length()).trim();
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > listOfTasks.size()) {
                return "you... don't have that task number???";
            }
            Task task = listOfTasks.getTask(taskNumber);
            task.markAsDone();
            storage.saveTasks(listOfTasks.getTasks());
            return "finally, that's done:\n  " + task;
        } catch (NumberFormatException exception) {
            return "*Yawns* You need to tell me which number to mark.. like: mark 2";
        }
    }

    /**
     * Marks the task selected by an {@code unmark <number>} command as not done.
     *
     * @param listOfTasks task list
     * @param command user command
     * @return result message for the user
     */
    private static String unmarkTask(TaskList listOfTasks, Storage storage, String command) {
        String taskNumberText = command.substring(CommandType.UNMARK.getWord().length()).trim();
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > listOfTasks.size()) {
                return "you... don't have that task number???";
            }
            Task task = listOfTasks.getTask(taskNumber);
            task.markAsUndone();
            storage.saveTasks(listOfTasks.getTasks());
            return "As productive as me... unmarked:\n  " + task;
        } catch (NumberFormatException exception) {
            return "*Yawns* You need to tell me which number to unmark.. like: unmark 2";
        }
    }
}
