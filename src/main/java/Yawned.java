import java.io.BufferedWriter;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.nio.file.AtomicMoveNotSupportedException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the Yawned chatbot application.
 */
public class Yawned {
    private static final Path SAVE_FILE = Path.of("data", "Yawned.txt");
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String banner = """
                 ========================
                          YAWNED
                    Your sleepy chatbot
                 ========================
                 """;
        printBreakLine();
        System.out.println(banner);
        List<Task> listOfTasks = loadTaskList();
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
     * separated by {@code |}.</p>
     *
     * @param listOfTasks task list to save
     */
    private static void saveTaskList(List<Task> listOfTasks) {
        Path temporaryFile = null;
        try {
            Files.createDirectories(SAVE_FILE.getParent());
            temporaryFile = SAVE_FILE.resolveSibling(SAVE_FILE.getFileName() + ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(temporaryFile)) {
                for (Task task : listOfTasks) {
                    writer.write(formatTaskForStorage(task));
                    writer.newLine();
                }
            }
            try {
                Files.move(temporaryFile, SAVE_FILE, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, SAVE_FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            System.out.println("OOPS!!! I couldn't save the task list.");
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {
                    // The temporary file will not affect the saved task list.
                }
            }
        }
    }

    /**
     * Loads the task list from the application's storage file.
     *
     * @return tasks stored on disk, or an empty list when no storage file exists
     */
    private static List<Task> loadTaskList() {
        List<Task> listOfTasks = new ArrayList<>();
        if (!Files.exists(SAVE_FILE)) {
            return listOfTasks;
        }

        try {
            List<String> storedTasks = Files.readAllLines(SAVE_FILE);
            for (int i = 0; i < storedTasks.size(); i++) {
                String storedTask = storedTasks.get(i);
                if (!storedTask.isBlank()) {
                    try {
                        listOfTasks.add(createTaskFromStorage(storedTask));
                    } catch (IllegalArgumentException | DateTimeException exception) {
                        System.out.println("OOPS!!! I skipped invalid saved task on line " + (i + 1) + ".");
                    }
                }
            }
        } catch (IOException exception) {
            System.out.println("OOPS!!! I couldn't read the saved task list. Starting with an empty list.");
        }
        return listOfTasks;
    }

    /**
     * Recreates one task from a line in the storage file.
     *
     * @param storedTask storage line describing the task
     * @return recreated task
     */
    private static Task createTaskFromStorage(String storedTask) {
        List<String> fields = splitStorageFields(storedTask);
        validateStorageFields(fields);
        Task task = switch (fields.get(0)) {
            case "T" -> new ToDo(fields.get(2));
            case "D" -> new Deadline(fields.get(2), LocalDateTime.parse(fields.get(3)));
            case "E" -> new Event(fields.get(2), LocalDateTime.parse(fields.get(3)),
                    LocalDateTime.parse(fields.get(4)));
            default -> throw new IllegalArgumentException("Cannot load task type: " + fields.get(0));
        };
        if (fields.get(1).equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Splits a storage line while restoring escaped pipes and backslashes.
     *
     * @param storedTask storage line to split
     * @return fields contained in the storage line
     */
    private static List<String> splitStorageFields(String storedTask) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        for (int i = 0; i < storedTask.length(); i++) {
            char character = storedTask.charAt(i);
            if (character == '\\' && i + 1 < storedTask.length()) {
                char nextCharacter = storedTask.charAt(i + 1);
                if (nextCharacter == '|' || nextCharacter == '\\') {
                    currentField.append(nextCharacter);
                    i++;
                    continue;
                }
            }
            if (character == '|') {
                fields.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(character);
            }
        }
        fields.add(currentField.toString().trim());
        return fields;
    }

    /**
     * Checks that a storage line has the fields needed to recreate a task.
     *
     * @param fields fields extracted from the storage line
     */
    private static void validateStorageFields(List<String> fields) {
        if (fields.size() < 2 || (!fields.get(1).equals("0") && !fields.get(1).equals("1"))) {
            throw new IllegalArgumentException("Invalid task status.");
        }
        int expectedFieldCount = switch (fields.get(0)) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new IllegalArgumentException("Invalid task type.");
        };
        if (fields.size() != expectedFieldCount) {
            throw new IllegalArgumentException("Invalid number of task fields.");
        }
        for (int i = 2; i < fields.size(); i++) {
            if (fields.get(i).isBlank()) {
                throw new IllegalArgumentException("Task fields cannot be empty.");
            }
        }
    }

    /**
     * Formats one task as a line in the storage file.
     *
     * @param task task to format
     * @return storage line for the task
     */
    private static String formatTaskForStorage(Task task) {
        String commonFields = task.getStatus().getStorageValue() + " | " + escapeStorageField(task.getDescription());
        return switch (task) {
            case ToDo _ -> "T | " + commonFields;
            case Deadline deadline -> "D | " + commonFields + " | " + escapeStorageField(deadline.getEndDate().toString());
            case Event event -> "E | " + commonFields + " | " + escapeStorageField(event.getStartDate().toString())
                    + " | " + escapeStorageField(event.getEndDate().toString());
            default -> throw new IllegalArgumentException("Cannot save task type: " + task.getClass().getSimpleName());
        };
    }

    /**
     * Escapes separators in one storage field.
     *
     * @param field field value to escape
     * @return escaped field value
     */
    private static String escapeStorageField(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|");
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
