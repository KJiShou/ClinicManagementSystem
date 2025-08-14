package utility;

import adt.LinkedQueue;
import adt.QueueInterface;

import java.util.Scanner;

public class MessageUI {
    Scanner scanner;
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
}
