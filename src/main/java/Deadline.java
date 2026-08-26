import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
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
     * Returns the deadline time for storage.
     *
     * @return deadline time
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: %s)".formatted(
                endDate.format(DISPLAY_DATE_FORMAT).toUpperCase(Locale.ENGLISH));
    }
}
