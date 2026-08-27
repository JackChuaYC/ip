import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Interprets user commands and creates tasks from task-creation commands.
 */
public class Parser {
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Identifies the type of user command.
     *
     * @param userInput complete user command
     * @return matching command type, or {@link CommandType#UNKNOWN}
     */
    public CommandType parseCommandType(String userInput) {
        return CommandType.fromInput(userInput);
    }

    /**
     * Creates the task described by a task-creation command.
     *
     * @param commandType type of task to create
     * @param command complete user command
     * @return the created task
     * @throws YawnedException if the command is incomplete or unknown
     */
    public Task parseTask(CommandType commandType, String command) throws YawnedException {
        String details = command.substring(commandType.getWord().length()).trim();
        return switch (commandType) {
        case TODO -> createToDo(details);
        case DEADLINE -> createDeadline(details);
        case EVENT -> createEvent(details);
        case UNKNOWN -> throw new YawnedException("urmmm, but I don't know what that means?? >:-(");
        default -> throw new IllegalArgumentException("Cannot create a task from command type: " + commandType);
        };
    }

    /**
     * Extracts a one-based task number from a task-selection command.
     *
     * @param commandType type of task-selection command
     * @param command complete user command
     * @return parsed task number; range validation is performed by the task list caller
     * @throws YawnedException if no whole-number task number was provided
     */
    public int parseTaskNumber(CommandType commandType, String command) throws YawnedException {
        String taskNumberText = command.substring(commandType.getWord().length()).trim();
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new YawnedException(missingTaskNumberMessage(commandType));
        }
    }

    /** Returns the appropriate task-number validation message for a command. */
    private static String missingTaskNumberMessage(CommandType commandType) {
        return switch (commandType) {
        case DELETE -> "*Yawns* You need to tell me which number to delete.. like: delete 2";
        case MARK -> "*Yawns* You need to tell me which number to mark.. like: mark 2";
        case UNMARK -> "*Yawns* You need to tell me which number to unmark.. like: unmark 2";
        default -> throw new IllegalArgumentException("Cannot select a task from command type: " + commandType);
        };
    }

    /** Creates a to-do from its command details. */
    private static ToDo createToDo(String details) throws YawnedException {
        if (details.isEmpty()) {
            throw new YawnedException("hey!!! The description of a todo cannot be empty. *yawns*");
        }
        return new ToDo(details);
    }

    /** Creates a deadline from its command details. */
    private static Deadline createDeadline(String details) throws YawnedException {
        int byIndex = details.indexOf(" /by");
        if (details.isEmpty() || details.startsWith("/by")) {
            throw new YawnedException(
                    "I just want to sleep... you forgot to provide a description for the deadline. "
                            + "Use: deadline <description> /by yyyy-MM-dd HHmm");
        }
        if (byIndex < 0) {
            throw new YawnedException(
                    "you woke me up for this? A deadline must include a /by time in yyyy-MM-dd HHmm format.");
        }
        String endDate = details.substring(byIndex + " /by".length()).trim();
        if (endDate.isEmpty()) {
            throw new YawnedException(
                    "you woke me up for this? A deadline must include a /by time in yyyy-MM-dd HHmm format.");
        }
        return new Deadline(details.substring(0, byIndex).trim(), parseDateTime(endDate));
    }

    /** Creates an event from its command details. */
    private static Event createEvent(String details) throws YawnedException {
        int fromIndex = details.indexOf(" /from");
        if (details.isEmpty() || details.startsWith("/from") || details.startsWith("/to")) {
            throw new YawnedException(
                    "I just want to sleep... you forgot to provide a description for the event. "
                            + "Use: event <description> /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm");
        }
        if (fromIndex < 0) {
            throw new YawnedException(
                    "Excuse me, An event must include /from and /to times in yyyy-MM-dd HHmm format.");
        }
        int toIndex = details.indexOf(" /to", fromIndex + " /from".length());
        if (toIndex < 0) {
            throw new YawnedException(
                    "Excuse me, An event must include /from and /to times in yyyy-MM-dd HHmm format.");
        }
        String fromDate = details.substring(fromIndex + " /from".length(), toIndex).trim();
        String toDate = details.substring(toIndex + " /to".length()).trim();
        if (fromDate.isEmpty() || toDate.isEmpty()) {
            throw new YawnedException(
                    "Excuse me, An event must include /from and /to times in yyyy-MM-dd HHmm format.");
        }
        return new Event(details.substring(0, fromIndex).trim(), parseDateTime(fromDate), parseDateTime(toDate));
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
}
