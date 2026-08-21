import java.util.Scanner;

/**
 * Entry point for the Yawned chatbot application.
 */
public class Yawned {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Task[] listOfTasks = new Task[100];
        int taskCounter = 0;
        String banner = "========================\n"
                + "         YAWNED\n"
                + "   Your sleepy chatbot\n"
                + "========================\n";
        printBreakLine();
        System.out.println(banner);
        String userInput = getUserInput(scanner, "*Yawns..* You woke me up...\nWhat do you want?\n");
        printBreakLine();
        while (!"bye".equals(userInput)) {
            if ("list".equals(userInput)) {
                printTaskList(listOfTasks, taskCounter);
                userInput = getUserInput(scanner, "");
            } else if (userInput.startsWith("mark ") || "mark".equals(userInput)) {
                userInput = getUserInput(scanner, markTask(listOfTasks, taskCounter, userInput));
            } else if (userInput.startsWith("unmark ") || "unmark".equals(userInput)) {
                userInput = getUserInput(scanner, unmarkTask(listOfTasks, taskCounter, userInput));
            } else if (userInput.startsWith("delete ") || "delete".equals(userInput)) {
                String taskNumberText = userInput.substring("delete".length()).trim();
                try {
                    int taskNumber = Integer.parseInt(taskNumberText);
                    if (taskNumber < 1 || taskNumber > taskCounter) {
                        userInput = getUserInput(scanner, "you... don't have that task number???");
                    } else {
                        Task deletedTask = deleteTask(listOfTasks, taskCounter, taskNumber);
                        taskCounter--;
                        userInput = getUserInput(scanner, deletedTaskMessage(deletedTask, taskCounter));
                    }
                } catch (NumberFormatException exception) {
                    userInput = getUserInput(scanner,
                            "*Yawns* You need to tell me which number to delete.. like: delete 2");
                }
            } else {
                try {
                    Task task = createTask(userInput);
                    int updatedTaskCounter = addTask(listOfTasks, task, taskCounter);
                    if (updatedTaskCounter == taskCounter) {
                        userInput = getUserInput(scanner, "sigh.. your task list is full.");
                    } else {
                        taskCounter = updatedTaskCounter;
                        userInput = getUserInput(scanner, addedTaskMessage(task, taskCounter));
                    }
                } catch (YawnedException exception) {
                    userInput = getUserInput(scanner, exception.getMessage());
                }
            }
            printBreakLine();
        }
        System.out.println("Bye.. I am going back to sleep.");
        printBreakLine();
    }

    /**
     *Prints the breakline for clearer "new command"
     */
    private static void printBreakLine() {
        System.out.println("____________________________________________________________\n");
    }

    /**
     * Gets input from user.
     *
     * @param scanner object to get input.
     * @param message output message to user.
     * @return string object.
     */
    private static String getUserInput(Scanner scanner, String message) {
        System.out.println(message);
        printBreakLine();
        return scanner.nextLine();
    }

    /**
     * Adds a task to the list of tasks
     * @param listOfTasks task list
     * @param task new task to be added
     * @param taskCounter current task counter
     * @return new task count (+1)
     */
    private static int addTask(Task[] listOfTasks, Task task, int taskCounter) {
        if (taskCounter == listOfTasks.length) {
            return taskCounter;
        }
        listOfTasks[taskCounter] = task;
        return ++taskCounter;
    }

    /**
     * Removes a task and shifts the following tasks forward to keep task numbers contiguous.
     *
     * @param listOfTasks task list
     * @param taskCounter number of stored tasks
     * @param taskNumber one-based number of the task to remove
     * @return removed task
     */
    private static Task deleteTask(Task[] listOfTasks, int taskCounter, int taskNumber) {
        int taskIndex = taskNumber - 1;
        Task deletedTask = listOfTasks[taskIndex];
        for (int i = taskIndex; i < taskCounter - 1; i++) {
            listOfTasks[i] = listOfTasks[i + 1];
        }
        listOfTasks[taskCounter - 1] = null;
        return deletedTask;
    }

    /**
     * Creates the task described by a task-creation command.
     *
     * @param command user command
     * @return the created task
     * @throws YawnedException if the command is incomplete or unknown
     */
    private static Task createTask(String command) throws YawnedException {
        if (command.startsWith("todo ") || "todo".equals(command)) {
            String description = command.substring("todo".length()).trim();
            if (description.isEmpty()) {
                throw new YawnedException("hey!!! The description of a todo cannot be empty. *yawns*");
            }
            return new ToDo(description);
        }
        if (command.startsWith("deadline ") || "deadline".equals(command)) {
            String details = command.substring("deadline".length()).trim();
            int byIndex = details.indexOf(" /by");
            if (details.isEmpty() || byIndex == 0) {
                throw new YawnedException("... The description of a deadline cannot be empty.");
            }
            if (byIndex < 0 || details.substring(byIndex + " /by".length()).trim().isEmpty()) {
                throw new YawnedException("you woke me up for this? A deadline must include a /by time.");
            }
            return new Deadline(details.substring(0, byIndex).trim(),
                    details.substring(byIndex + " /by".length()).trim());
        }
        if (command.startsWith("event ") || "event".equals(command)) {
            String details = command.substring("event".length()).trim();
            int fromIndex = details.indexOf(" /from");
            int toIndex = details.indexOf(" /to", fromIndex + " /from".length());
            if (details.isEmpty() || fromIndex == 0) {
                throw new YawnedException("*yawns* The description of an event cannot be empty.");
            }
            if (fromIndex < 0 || toIndex < 0
                    || details.substring(fromIndex + " /from".length(), toIndex).trim().isEmpty()
                    || details.substring(toIndex + " /to".length()).trim().isEmpty()) {
                throw new YawnedException("Excuse me, An event must include /from and /to times.");
            }
            return new Event(details.substring(0, fromIndex).trim(),
                    details.substring(fromIndex + " /from".length(), toIndex).trim(),
                    details.substring(toIndex + " /to".length()).trim());
        }
        throw new YawnedException("urmmm, but I don't know what that means?? >:-(");
    }

    /**
     * Formats the confirmation shown after a task is successfully added.
     *
     * @param task added task
     * @param taskCounter updated number of tasks
     * @return confirmation message
     */
    private static String addedTaskMessage(Task task, int taskCounter) {
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + taskCounter + " tasks in the list.";
    }

    /**
     * Formats the confirmation shown after a task is deleted.
     *
     * @param task deleted task
     * @param taskCounter updated number of tasks
     * @return confirmation message
     */
    private static String deletedTaskMessage(Task task, int taskCounter) {
        return "fine. I removed this task:\n  " + task
                + "\nNow you have " + taskCounter + " tasks in the list.";
    }

    /**
     * prints the task list
     * @param listOfTasks list of tasks
     * @param taskCounter number of items in the tasklist.
     */
    private static void printTaskList(Task[] listOfTasks, int taskCounter) {
        if (taskCounter == 0) {
            System.out.println("No Tasks!");
            return;
        }
        System.out.println("Here you go, the tasks in your list:");
        for (int i = 0; i < taskCounter; i++) {
            System.out.printf("%d.%s%n", i + 1, listOfTasks[i]);
        }
    }

    /**
     * Marks the task selected by a {@code mark <number>} command as done.
     *
     * @param listOfTasks task list
     * @param taskCounter number of stored tasks
     * @param command user command
     * @return result message for the user
     */
    private static String markTask(Task[] listOfTasks, int taskCounter, String command) {
        String taskNumberText = command.substring("mark".length()).trim();
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > taskCounter) {
                return "you... don't have that task number???";
            }
            Task task = listOfTasks[taskNumber - 1];
            task.markAsDone();
            return "finally, that's done:\n  " + task;
        } catch (NumberFormatException exception) {
            return "*Yawns* You need to tell me which number to mark.. like: mark 2";
        }
    }

    /**
     * Marks the task selected by an {@code unmark <number>} command as not done.
     *
     * @param listOfTasks task list
     * @param taskCounter number of stored tasks
     * @param command user command
     * @return result message for the user
     */
    private static String unmarkTask(Task[] listOfTasks, int taskCounter, String command) {
        String taskNumberText = command.substring("unmark".length()).trim();
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > taskCounter) {
                return "you... don't have that task number???";
            }
            Task task = listOfTasks[taskNumber - 1];
            task.markAsUndone();
            return "As productive as me... unmarked:\n  " + task;
        } catch (NumberFormatException exception) {
            return "*Yawns* You need to tell me which number to unmark.. like: unmark 2";
        }
    }
}
