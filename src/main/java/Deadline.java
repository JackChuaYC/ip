public class Deadline extends Task{
    private String endDate;

    /**
     * Creates a deadline task with the given description and a deadline.
     *
     * @param description description of the task
     */
    public Deadline(String description, String endDate) {
        super(description);
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + "(by: %s)".formatted(endDate);
    }
}
