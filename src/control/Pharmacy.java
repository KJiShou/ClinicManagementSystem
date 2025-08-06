package control;

import adt.DictionaryInterface;
import adt.HashedDictionary;
import boundary.PharmacyUI;
import entity.pharmacyManagement.Medicine;
import entity.pharmacyManagement.MedicineKey;
import utility.GenerateMedicineData;
import utility.MessageUI;

import java.text.SimpleDateFormat;

public class Pharmacy {
    DictionaryInterface<MedicineKey, Medicine> meds;
    PharmacyUI UI;
    Pharmacy() {
        try {
            HashedDictionary<MedicineKey, Medicine> meds =
                    GenerateMedicineData.createMedicineTable();
            UI = new PharmacyUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void main() {
        Integer choice = UI.mainMenu();
        System.out.println(choice);
    }

//    public static void test() {
//        try {
//            // Lookup a specific batch:
//            MedicineKey lookup = new MedicineKey("Ibuprofen",
//                    "Advil",
//                    new SimpleDateFormat("yyyy-MM-dd")
//                            .parse("2025-11-30"));
//            Medicine ibup = meds.getValue(lookup);
//            System.out.println("Found: " + ibup.getName() +
//                    " (" + ibup.getBrand() + ")");
//
//            // Or iterate them all:
//            for (adt.Entry<MedicineKey, Medicine> e : meds) {
//                System.out.println(
//                        e.getValue().getName()
//                                + " — expires " +
//                                new SimpleDateFormat("yyyy-MM-dd")
//                                        .format(e.getValue().getExpiryDate())
//                );
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
