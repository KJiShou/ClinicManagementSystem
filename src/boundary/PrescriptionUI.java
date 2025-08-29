// Tan Yew Shen
package boundary;

import java.util.*;
import java.io.IOException;
import java.util.Scanner;

import adt.*;
import entity.pharmacyManagement.Prescription;
import entity.pharmacyManagement.SalesItem;
import utility.MessageUI;

public class PrescriptionUI {

    private QueueInterface<String> choiceQueue;
    private MessageUI UI;
    private static final int PAGE_SIZE = 5;
    private Scanner scanner;

    public PrescriptionUI() {
        choiceQueue = new LinkedQueue<>();
        UI = new MessageUI();
        scanner = new Scanner(System.in);
    }

    public int prescriptionMenu() throws IOException {
        choiceQueue.clear();
        choiceQueue.enqueue("Add Prescription");
        choiceQueue.enqueue("View Prescriptions");
        choiceQueue.enqueue("Edit Existing Prescription");
        choiceQueue.enqueue("Prescription Reports");

        MessageUI messageUI = new MessageUI();

        return UI.mainUI("--- Prescription Menu ---", choiceQueue);
    }

}