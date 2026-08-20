/**
 * Entry point for the Yawned chatbot application.
 */
import java.util.Scanner;

public class Yawned {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] listOfTasks = new String[100];
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
     * @param listOfTask task list
     * @param task new task to be added
     * @param taskCounter current task counter
     * @return new task count (+1)
     */
    private static int addTask(String[] listOfTasks, String task,int taskCounter) {
        listOfTasks[taskCounter] = task;
        return ++taskCounter;
    }

    /**
     * prints the task list
     * @param listOfTasks list of tasks
     * @param taskCounter number of items in the tasklist.
     */
    private static void printTaskList(String[] listOfTasks, int taskCounter) {
        if (taskCounter == 0) {
            System.out.println("No Tasks!");
            return;
        }
        for (int i = 0; i < taskCounter; i++) {
            System.out.printf("%d. %s%n", i+1, listOfTasks[i]);
        }
    }
}
