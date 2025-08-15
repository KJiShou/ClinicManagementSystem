package utility;

import adt.LinkedQueue;
import adt.QueueInterface;

import java.util.Scanner;

public class MessageUI {
    static Scanner scanner;
    Integer choice;
    public MessageUI() {
        scanner = new Scanner(System.in);
    }

    public void getChoice(Integer maxChoice) {
        while (true) {
            try {
                System.out.print("Enter your choice > ");
                choice = scanner.nextInt();
                if (choice == 999) return;
                if (choice <= 0 || choice > maxChoice) {
                    System.out.println("Invalid choice. Pls Enter again.");
                } else {
                    return;
                }
            } catch (Exception e) {
                scanner.nextLine();
                System.out.println("Invalid choice. Pls Enter again.");
            }
        }
    }

    public static String center(String text, int width) {
        if (text == null || width <= text.length()) {
            return text;
        }
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) +
                text;
    }

    public Integer mainUI(String title, QueueInterface<String> choiceQueue) {
        int size = choiceQueue.size();
        System.out.println("\n\n\n\n\n\n\n\n\n\n");
        System.out.println("+------------------------------------------------------+");
        System.out.printf("| %-52s |\n", center(title, 52));
        System.out.println("+------------------------------------------------------+");
        int count = 0;
        while (!choiceQueue.isEmpty()) {
            System.out.printf("| [ %d ] %-46s |\n", ++count, choiceQueue.dequeue());
            System.out.println("+------------------------------------------------------+");
        }
        System.out.println("| [999] Exit                                           |");
        System.out.println("+------------------------------------------------------+");
        getChoice(size);
        return choice;
    }

    /**
     * Utility method to ask for a positive integer from user
     */
    public static int askPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v > 0) return v;
            } catch (Exception ignored) {}
            System.out.println("Please enter a positive integer.");
        }
    }

    /**
     * Utility method to ask for a positive double from user
     */
    public static double askPositiveDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                double v = Double.parseDouble(s);
                if (v > 0) return v;
            } catch (Exception ignored) {}
            System.out.println("Please enter a positive number.");
        }
    }

}
