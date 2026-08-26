import java.time.LocalDate;

/**
 * Represents a task that takes place between a start and end time.
 */
public class Event extends Task {
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
     * Creates an event from ISO date strings during the transition to parsing
     * dates in the chatbot.
     *
     * @param description description of the event
     * @param startDate event start date in {@code yyyy-MM-dd} format
     * @param endDate event end date in {@code yyyy-MM-dd} format
     * @deprecated pass {@link LocalDate} objects instead
     */
    @Deprecated
    public Event(String description, String startDate, String endDate) {
        this(description, LocalDate.parse(startDate), LocalDate.parse(endDate));
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
        return "[E]" + super.toString() + " (from: %s to: %s)".formatted(startDate, endDate);
    }
}
