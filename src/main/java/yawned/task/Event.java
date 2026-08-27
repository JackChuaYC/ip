package yawned.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that takes place between a start and end time.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(
            "MMM dd uuuu HHmm", Locale.ENGLISH);
    private final LocalDateTime endDate;
    private final LocalDateTime startDate;

    /**
     * Creates an event with the given description, start time, and end time.
     *
     * @param description description of the event
     * @param startDate event start time
     * @param endDate event end time
     */
    public Event(String description, LocalDateTime startDate, LocalDateTime endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the event start time for storage.
     *
     * @return event start time
     */
    public LocalDateTime getStartDate() {
        return startDate;
    }

    /**
     * Returns the event end time for storage.
     *
     * @return event end time
     */
    public LocalDateTime getEndDate() {
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
    private static String formatDate(LocalDateTime date) {
        return date.format(DISPLAY_DATE_TIME_FORMAT).toUpperCase(Locale.ENGLISH);
    }
}
