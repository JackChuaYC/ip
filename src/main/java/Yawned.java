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
            } else {
                taskCounter = addTask(listOfTasks, userInput, taskCounter);
                userInput = getUserInput(scanner, "added: " + userInput);
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
    private static int addTask(Task[] listOfTasks, String task, int taskCounter) {
        if (taskCounter == listOfTasks.length) {
            System.out.println("Your task list is full.");
            return taskCounter;
        }
        listOfTasks[taskCounter] = new Task(task);
        return ++taskCounter;
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
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < taskCounter; i++) {
            System.out.printf("%d. %s%n", i + 1, listOfTasks[i]);
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
}
