package control;

import adt.HashedDictionary;
import adt.ListInterface;
import adt.ArrayList;
import boundary.ConsultationUI;

public class Consultation {
    ListInterface<ConsultationUI> consultationList;
    ConsultationUI UI;

    Consultation() {
        try {
            ArrayList<ConsultationUI> consultationList = new ArrayList();
            UI = new ConsultationUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void main() {
        Integer choice = UI.menu();
        System.out.println(choice);
    }
}
