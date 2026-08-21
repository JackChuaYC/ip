public class Event extends Task{
    private final String endDate;
    private final String startDate;

    public Event(String description, String startDate, String endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + "(from: %s to %s)".formatted(startDate, endDate);
    }
}
