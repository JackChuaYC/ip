/**
 * Represents a task that takes place between a start and end time.
 */
public class Event extends Task {
    private final String endDate;
    private final String startDate;

    /**
     * Creates an event with the given description, start time, and end time.
     *
     * @param description description of the event
     * @param startDate event start time
     * @param endDate event end time
     */
    public Event(String description, String startDate, String endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the event start time for storage.
     *
     * @return event start time
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Returns the event end time for storage.
     *
     * @return event end time
     */
    public String getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: %s to: %s)".formatted(startDate, endDate);
    }
}
