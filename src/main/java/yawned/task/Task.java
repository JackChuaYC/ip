package yawned.task;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    private final String description;
    private TaskStatus status;

    public Task(String description) {
        this.description = description;
        this.status = TaskStatus.NOT_DONE;
    }

    public void markAsDone() {
        this.status = TaskStatus.DONE;
    }

    public void markAsUndone() {
        this.status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns this task's description for storage.
     *
     * @return task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns this task's completion status for storage.
     *
     * @return current completion status
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Returns the task description together with its completion status.
     *
     * @return task status and description
     */
    @Override
    public String toString() {
        return status.getIcon() + " " + description;
    }
}
