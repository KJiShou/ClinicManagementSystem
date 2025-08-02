package boundary;

import java.util.Scanner;

public class ConsultationUI {
    Scanner scanner = new Scanner(System.in);

    public int getMenuChoice() {
        System.out.println("1. Add Consultation");
        System.out.println("2. View Consultation");
        System.out.println("3. Update Consultation");
        System.out.println("4. Delete Consultation");
        System.out.println("5. Search Consultation");
        System.out.println("6. Exit");
        int choice = scanner.nextInt();
        System.out.println();
        return choice;
    }
}
