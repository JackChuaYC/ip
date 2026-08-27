package yawned.ui;

import java.util.Scanner;

import yawned.task.TaskList;

/**
 * Handles all console interaction with the user.
 */
public class Ui {
    private static final String BANNER = """
            ========================
                     YAWNED
               Your sleepy chatbot
            ========================
            """;

    private final Scanner scanner;

    /**
     * Creates a user interface that reads commands from the given scanner.
     *
     * @param scanner source of user input
     */
    public Ui(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Shows the chatbot greeting.
     */
    public void showWelcome() {
        showBreakLine();
        System.out.println(BANNER);
    }

    /**
     * Shows a message and reads the next command from the user.
     *
     * @param message message to display before accepting input
     * @return the user's command
     */
    public String readCommand(String message) {
        System.out.println(message);
        showBreakLine();
        return scanner.nextLine();
    }

    /**
     * Shows a message without requesting input.
     *
     * @param message message to display
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Shows the current task list.
     *
     * @param taskList tasks to display
     */
    public void showTaskList(TaskList taskList) {
        if (taskList.isEmpty()) {
            System.out.println("No Tasks!");
            return;
        }
        System.out.println("Here you go, the tasks in your list:");
        for (int i = 1; i <= taskList.size(); i++) {
            System.out.printf("%d.%s%n", i, taskList.getTask(i));
        }
    }

    /**
     * Shows the separator between console interactions.
     */
    public void showBreakLine() {
        System.out.println("____________________________________________________________\n");
    }
}
