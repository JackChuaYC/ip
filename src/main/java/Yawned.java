/**
 * Entry point for the Yawned chatbot application.
 */
import java.util.Scanner;

public class Yawned {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String banner = "========================\n"
                + "         YAWNED\n"
                + "   Your sleepy chatbot\n"
                + "========================\n";
        printBreakLine();
        System.out.println(banner);
        String userInput = getUserInput(scanner, "*Yawns..* You woke me up...\nWhat do you want?\n");
        printBreakLine();
        while (!userInput.equals("bye")) {
            userInput = getUserInput(scanner, userInput);
            printBreakLine();
        }
        System.out.println("");
        printBreakLine();
    }

    /*
     *Prints the breakline for clearer "new command"
     */
    public static void printBreakLine() {
        System.out.println("____________________________________________________________\n");
    }

    /*
     */
    public static String getUserInput(Scanner scanner, String message) {
        System.out.println(message);
        printBreakLine();
        return scanner.nextLine();
    }
}
