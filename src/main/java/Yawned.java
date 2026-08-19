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
        while (!userInput.equals("bye")) {
            if (userInput.equals("list")) {
                printTaskList(listOfTasks, taskCounter);
                userInput = getUserInput(scanner, "");
            } else {
                taskCounter = addTask(listOfTasks, userInput, taskCounter);
                userInput = getUserInput(scanner, String.format("added: %s", userInput));
            }
            printBreakLine();
        }
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
     * @param Scanner scanner object to get input.
     * @param String message output message to user.
     * @return string object.
     */
    private static String getUserInput(Scanner scanner, String message) {
        System.out.println(message);
        printBreakLine();
        return scanner.nextLine();
    }

    private static int addTask(String[] listOfTasks, String task,int taskCounter) {
        listOfTasks[taskCounter] = task;
        return ++taskCounter;
    }

    private static void printTaskList(String[] listOfTasks, int taskCounter) {
        for (int i = 0; i < taskCounter; i++) {
            System.out.printf("%d. %s%n", i+1, listOfTasks[i]);
        }
    }
}
