// Kong Ji Shou
package boundary;

import adt.LinkedQueue;
import adt.QueueInterface;
import utility.MessageUI;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class MainUI {
    QueueInterface<String> choiceQueue;
    MessageUI UI;
    int pageSize;
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");
    Scanner scanner;

    public MainUI() {
        choiceQueue = new LinkedQueue<String>();
        UI = new MessageUI();
        pageSize = 5;
        scanner = new Scanner(System.in);
    }

    public Integer mainMenu() throws IOException {
        // choice 1
        choiceQueue.enqueue("Patient");
        choiceQueue.enqueue("Consultation");
        choiceQueue.enqueue("Pharmacy");
        choiceQueue.enqueue("Duty Schedule");
        choiceQueue.enqueue("Appointment");
        choiceQueue.enqueue("Staff Management");
        choiceQueue.enqueue("Doctor Management");
        choiceQueue.enqueue("Logout");

        return UI.mainUI("Welcome to Clinic Management System", choiceQueue);
    }
}
