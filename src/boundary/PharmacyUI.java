package boundary;

import adt.LinkedQueue;
import adt.QueueInterface;
import utility.MessageUI;

import java.util.Scanner;

public class PharmacyUI {
    QueueInterface<String> choiceQueue;
    MessageUI UI;

    public PharmacyUI() {
        choiceQueue = new LinkedQueue<String>();
        UI = new MessageUI();
    }

    public Integer mainMenu() {
        // choice 1
        choiceQueue.enqueue("Check Sales Item");
        choiceQueue.enqueue("Stock In Sales Item");
        choiceQueue.enqueue("Update Sales Item details");
        choiceQueue.enqueue("Stock Out Sales Item");

        return UI.mainUI("Welcome to Pharmacy Management System", choiceQueue);
    }
}
