/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private final String endDate;

    /**
     * Creates a deadline task with the given description and a deadline.
     *
     * @param description description of the task
     */
    public Deadline(String description, String endDate) {
        super(description);
        this.endDate = endDate;
    }

    /**
     * Returns the deadline time for storage.
     *
     * @return deadline time
     */
    public String getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: %s)".formatted(endDate);
    }
}
