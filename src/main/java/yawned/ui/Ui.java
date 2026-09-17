package yawned.ui;

import java.util.Scanner;

/**
 * Handles all console interaction with the user.
 */
public class Ui {
    private static final String BANNER = """
            ========================
                     YAWNED
             Reluctantly organized
            ========================
            """;

    private final Scanner scanner;

    /**
     * Creates a user interface that reads commands from the given scanner.
     *
     * @param scanner Source of user input.
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
     * @param message Message to display before accepting input.
     * @return The user's command.
     */
    public String readCommand(String message) {
        System.out.println(message);
        showBreakLine();
        return scanner.nextLine();
    }

    /**
     * Shows a message without requesting input.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Shows the separator between console interactions.
     */
    public void showBreakLine() {
        System.out.println("____________________________________________________________\n");
    }
}
