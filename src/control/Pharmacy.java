package control;

import adt.HashedDictionary;
import entity.pharmacyManagement.Medicine;
import entity.pharmacyManagement.MedicineKey;
import utility.GenerateMedicineData;

import java.text.SimpleDateFormat;

public class Pharmacy {
    public static void test() {
        try {
            // Grab your fully populated dictionary:
            HashedDictionary<MedicineKey, Medicine> meds =
                    GenerateMedicineData.createMedicineTable();

            // Lookup a specific batch:
            MedicineKey lookup = new MedicineKey("Ibuprofen",
                    "Advil",
                    new SimpleDateFormat("yyyy-MM-dd")
                            .parse("2025-11-30"));
            Medicine ibup = meds.getValue(lookup);
            System.out.println("Found: " + ibup.getName() +
                    " (" + ibup.getBrand() + ")");

            // Or iterate them all:
            for (adt.Entry<MedicineKey, Medicine> e : meds) {
                System.out.println(
                        e.getValue().getName()
                                + " — expires " +
                                new SimpleDateFormat("yyyy-MM-dd")
                                        .format(e.getValue().getExpiryDate())
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
