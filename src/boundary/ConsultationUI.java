package boundary;

import adt.QueueInterface;
import adt.LinkedQueue;
import utility.MessageUI;

public class ConsultationUI {
    QueueInterface<String> choiceQueue;
    MessageUI UI;

    public ConsultationUI() {
        choiceQueue = new LinkedQueue<String>();
        UI = new MessageUI();
    }

    public int menu() {
        choiceQueue.enqueue("View Consultation");
        choiceQueue.enqueue("Add Consultation");
        choiceQueue.enqueue("Update Consultation");
        choiceQueue.enqueue("Delete Consultation");
        choiceQueue.enqueue("Search Consultation");

        return UI.mainUI("Welcome to Consultation Menu", choiceQueue);
    }
}
