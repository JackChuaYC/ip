import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that takes place between a start and end time.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private final LocalDate endDate;
    private final LocalDate startDate;

    /**
     * Creates an event with the given description, start time, and end time.
     *
     * @param description description of the event
     * @param startDate event start time
     * @param endDate event end time
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the event start time for storage.
     *
     * @return event start time
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the event end time for storage.
     *
     * @return event end time
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: %s to: %s)".formatted(
                formatDate(startDate), formatDate(endDate));
    }

    /**
     * Formats a date for display in the task list.
     *
     * @param date date to format
     * @return formatted date
     */
    private static String formatDate(LocalDate date) {
        return date.format(DISPLAY_DATE_FORMAT).toUpperCase(Locale.ENGLISH);
    }
}
