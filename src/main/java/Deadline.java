import java.time.LocalDate;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private final LocalDate endDate;

    /**
     * Creates a deadline task with the given description and a deadline.
     *
     * @param description description of the task
     */
    public Deadline(String description, LocalDate endDate) {
        super(description);
        this.endDate = endDate;
    }

    /**
     * Creates a deadline task from an ISO date string during the transition to
     * parsing dates in the chatbot.
     *
     * @param description description of the task
     * @param endDate deadline in {@code yyyy-MM-dd} format
     * @deprecated pass a {@link LocalDate} instead
     */
    @Deprecated
    public Deadline(String description, String endDate) {
        this(description, LocalDate.parse(endDate));
    }

    /**
     * Returns the deadline time for storage.
     *
     * @return deadline time
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: %s)".formatted(endDate);
    }
}
