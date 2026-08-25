import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the Yawned chatbot application.
 */
public class Yawned {
    private static final Path SAVE_FILE = Path.of("data", "Yawned.txt");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Task> listOfTasks = new ArrayList<>();
        String banner = "========================\n"
                + "         YAWNED\n"
                + "   Your sleepy chatbot\n"
                + "========================\n";
        printBreakLine();
        System.out.println(banner);
        String userInput = getUserInput(scanner, "*Yawns..* You woke me up...\nWhat do you want?\n");
        printBreakLine();
        CommandType commandType = CommandType.fromInput(userInput);
        while (commandType != CommandType.BYE) {
            switch (commandType) {
            case LIST:
                printTaskList(listOfTasks);
                userInput = getUserInput(scanner, "");
                break;
            case MARK:
                userInput = getUserInput(scanner, markTask(listOfTasks, userInput));
                break;
            case UNMARK:
                userInput = getUserInput(scanner, unmarkTask(listOfTasks, userInput));
                break;
            case DELETE:
                userInput = getUserInput(scanner, deleteTaskMessage(listOfTasks, userInput));
                break;
            case TODO:
            case DEADLINE:
            case EVENT:
            case UNKNOWN:
                try {
                    Task task = createTask(commandType, userInput);
                    addTask(listOfTasks, task);
                    userInput = getUserInput(scanner, addedTaskMessage(task, listOfTasks.size()));
                } catch (YawnedException exception) {
                    userInput = getUserInput(scanner, exception.getMessage());
                }
                break;
            default:
                throw new IllegalStateException("Unexpected command type: " + commandType);
            }
            printBreakLine();
            commandType = CommandType.fromInput(userInput);
        }
        System.out.println("Bye.. I am going back to sleep.");
        printBreakLine();
    }

    /**
     *Prints the breakline for clearer "new command"
     */
    private static void printBreakLine() {
        System.out.println("____________________________________________________________\n");
    }

    /**
     * Gets input from user.
     *
     * @param scanner object to get input.
     * @param message output message to user.
     * @return string object.
     */
    private static String getUserInput(Scanner scanner, String message) {
        System.out.println(message);
        printBreakLine();
        return scanner.nextLine();
    }

    /**
     * Adds a task to the list of tasks
     * @param listOfTasks task list
     * @param task new task to be added
     */
    private static void addTask(List<Task> listOfTasks, Task task) {
        listOfTasks.add(task);
        saveTaskList(listOfTasks);
    }

    /**
     * Removes a task while keeping the remaining task numbers contiguous.
     *
     * @param listOfTasks task list
     * @param taskNumber one-based number of the task to remove
     * @return removed task
     */
    private static Task deleteTask(List<Task> listOfTasks, int taskNumber) {
        Task deletedTask = listOfTasks.remove(taskNumber - 1);
        saveTaskList(listOfTasks);
        return deletedTask;
    }

    /**
     * Saves all tasks to the application's storage file.
     *
     * <p>Each line contains a task type, status, description, and any time fields,
     * separated by {@code |}. This format is prepared for a later loading feature.</p>
     *
     * @param listOfTasks task list to save
     */
    private static void saveTaskList(List<Task> listOfTasks) {
        try {
            Files.createDirectories(SAVE_FILE.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(SAVE_FILE)) {
                for (Task task : listOfTasks) {
                    writer.write(formatTaskForStorage(task));
                    writer.newLine();
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save the task list.", exception);
        }
    }

    /**
     * Formats one task as a line in the storage file.
     *
     * @param task task to format
     * @return storage line for the task
     */
    private static String formatTaskForStorage(Task task) {
        String commonFields = task.getStatus().getStorageValue() + " | " + task.getDescription();
        if (task instanceof ToDo) {
            return "T | " + commonFields;
        }
        if (task instanceof Deadline deadline) {
            return "D | " + commonFields + " | " + deadline.getEndDate();
        }
        if (task instanceof Event event) {
            return "E | " + commonFields + " | " + event.getStartDate() + " | " + event.getEndDate();
        }
        throw new IllegalArgumentException("Cannot save task type: " + task.getClass().getSimpleName());
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
            String description = details;
            if (description.isEmpty()) {
                throw new YawnedException("hey!!! The description of a todo cannot be empty. *yawns*");
            }
            return new ToDo(description);
        case DEADLINE:
            int byIndex = details.indexOf(" /by");
            if (details.isEmpty() || details.startsWith("/by")) {
                throw new YawnedException(
                        "I just want to sleep... you forgot to provide a description for the deadline");
            }
            if (byIndex < 0 || details.substring(byIndex + " /by".length()).trim().isEmpty()) {
                throw new YawnedException("you woke me up for this? A deadline must include a /by time.");
            }
            return new Deadline(details.substring(0, byIndex).trim(),
                    details.substring(byIndex + " /by".length()).trim());
        case EVENT:
            int fromIndex = details.indexOf(" /from");
            int toIndex = details.indexOf(" /to", fromIndex + " /from".length());
            if (details.isEmpty() || details.startsWith("/from") || details.startsWith("/to")) {
                throw new YawnedException(
                        "I just want to sleep... you forgot to provide a description for the event");
            }
            if (fromIndex < 0 || toIndex < 0
                    || details.substring(fromIndex + " /from".length(), toIndex).trim().isEmpty()
                    || details.substring(toIndex + " /to".length()).trim().isEmpty()) {
                throw new YawnedException("Excuse me, An event must include /from and /to times.");
            }
            return new Event(details.substring(0, fromIndex).trim(),
                    details.substring(fromIndex + " /from".length(), toIndex).trim(),
                    details.substring(toIndex + " /to".length()).trim());
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
    private static String deleteTaskMessage(List<Task> listOfTasks, String command) {
        String taskNumberText = command.substring(CommandType.DELETE.getWord().length()).trim();
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > listOfTasks.size()) {
                return "you... don't have that task number???";
            }
            Task deletedTask = deleteTask(listOfTasks, taskNumber);
            return deletedTaskMessage(deletedTask, listOfTasks.size());
        } catch (NumberFormatException exception) {
            return "*Yawns* You need to tell me which number to delete.. like: delete 2";
        }
    }

    /**
     * prints the task list
     * @param listOfTasks list of tasks
     */
    private static void printTaskList(List<Task> listOfTasks) {
        if (listOfTasks.isEmpty()) {
            System.out.println("No Tasks!");
            return;
        }
        System.out.println("Here you go, the tasks in your list:");
        for (int i = 0; i < listOfTasks.size(); i++) {
            System.out.printf("%d.%s%n", i + 1, listOfTasks.get(i));
        }
    }

    /**
     * Marks the task selected by a {@code mark <number>} command as done.
     *
     * @param listOfTasks task list
     * @param command user command
     * @return result message for the user
     */
    private static String markTask(List<Task> listOfTasks, String command) {
        String taskNumberText = command.substring(CommandType.MARK.getWord().length()).trim();
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > listOfTasks.size()) {
                return "you... don't have that task number???";
            }
            Task task = listOfTasks.get(taskNumber - 1);
            task.markAsDone();
            saveTaskList(listOfTasks);
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
    private static String unmarkTask(List<Task> listOfTasks, String command) {
        String taskNumberText = command.substring(CommandType.UNMARK.getWord().length()).trim();
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > listOfTasks.size()) {
                return "you... don't have that task number???";
            }
            Task task = listOfTasks.get(taskNumber - 1);
            task.markAsUndone();
            saveTaskList(listOfTasks);
            return "As productive as me... unmarked:\n  " + task;
        } catch (NumberFormatException exception) {
            return "*Yawns* You need to tell me which number to unmark.. like: unmark 2";
        }
    }
}
