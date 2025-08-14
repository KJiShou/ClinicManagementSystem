package utility;

import adt.Entry;
import adt.HashedDictionary;
import entity.pharmacyManagement.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class GeneratePharmacyData {
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");

    /** Predefined the company and medicine data */
    private static Company[] createSampleCompanies() {
        return new Company[] {
                new Company(UUID.randomUUID(),
                        "Pfizer",
                        "235 East 42nd St, New York, NY",
                        "info@pfizer.com",
                        "1-800-555-0100"),
                new Company(UUID.randomUUID(),
                        "GlaxoSmithKline",
                        "980 Great West Rd, Brentford, UK",
                        "contact@gsk.com",
                        "+44-20-8047-5000"),
                new Company(UUID.randomUUID(),
                        "Novartis",
                        "Lichtstrasse 35, 4056 Basel, Switzerland",
                        "service@novartis.com",
                        "+41-61-324-1111"),
                new Company(UUID.randomUUID(),
                        "Roche",
                        "Grenzacherstrasse 124, Basel, Switzerland",
                        "contactus@roche.com",
                        "+41-61-688-1111"),
                new Company(UUID.randomUUID(),
                        "Johnson & Johnson",
                        "One Johnson & Johnson Plaza, New Brunswick, NJ",
                        "info@jnj.com",
                        "1-800-555-0155")
        };
    }

    private static Medicine[] createSampleMedicines() throws ParseException {
        Company[] c = createSampleCompanies();

        return new Medicine[] {
                new Medicine(UUID.randomUUID(),
                        "Paracetamol", 10, 3.50,
                        "Pain relief and fever reducer", "tablet",
                        c[0],  // Pfizer
                        "Panadol", "500 mg",
                        DATE_FMT.parse("2026-12-31")),

                new Medicine(UUID.randomUUID(),
                        "Ibuprofen",10, 4.20,
                        "NSAID for pain and inflammation", "tablet",
                        c[1],  // GSK
                        "Advil", "200 mg",
                        DATE_FMT.parse("2025-11-30")),

                new Medicine(UUID.randomUUID(),
                        "Amoxicillin", 100,12.00,
                        "Broad-spectrum antibiotic", "capsule",
                        c[2],  // Novartis
                        "Amoxil", "500 mg",
                        DATE_FMT.parse("2026-06-30")),

                new Medicine(UUID.randomUUID(),
                        "Cetirizine", 100,2.75,
                        "Antihistamine for allergy relief", "tablet",
                        c[3],  // Roche
                        "Zyrtec", "10 mg",
                        DATE_FMT.parse("2027-01-15")),

                new Medicine(UUID.randomUUID(),
                        "Metformin",100, 8.50,
                        "Type 2 diabetes management", "tablet",
                        c[4],  // J&J
                        "Glucophage", "500 mg",
                        DATE_FMT.parse("2026-03-31")),

                new Medicine(UUID.randomUUID(),
                        "Omeprazole", 100,9.00,
                        "Proton-pump inhibitor for GERD", "capsule",
                        c[0],  // Pfizer
                        "Prilosec", "20 mg",
                        DATE_FMT.parse("2026-09-30")),

                new Medicine(UUID.randomUUID(),
                        "Atorvastatin", 1,15.00,
                        "Cholesterol-lowering statin", "tablet",
                        c[1],  // GSK
                        "Lipitor", "10 mg",
                        DATE_FMT.parse("2027-05-31")),

                new Medicine(UUID.randomUUID(),
                        "Atorvastatin", 1,15.00,
                        "Cholesterol-lowering statin", "tablet",
                        c[1],  // GSK
                        "Lipitor", "10 mg",
                        DATE_FMT.parse("2027-06-30"))
        };
    }

    public static HashedDictionary<String, BloodTube> createBloodTubeInventory() throws ParseException {
        HashedDictionary<String, BloodTube> inventory = new HashedDictionary<>();
        Company[] c = createSampleCompanies();

        BloodTube edtaPurpleTube = new BloodTube(UUID.randomUUID(),
                "EDTA Purple Tube 4ml",
                0.75, 500,
                "Used for CBC, HbA1c. Contains EDTA anticoagulant",
                c[2],
                DATE_FMT.parse("2026-01-01"),
                4.0, "Purple", "EDTA K2");

        BloodTube sodiumCitrateBlueTube = new BloodTube(UUID.randomUUID(),
                "Sodium Citrate Blue Tube 2.7ml",
                0.75, 300,
                "Used for Coagulation profile (PT/APTT). Contains Sodium Citrate.",
                c[2], DATE_FMT.parse("2025-09-10"),
                2.7, "Blue", "Sodium Citrate");

        BloodTube serumRedTube = new BloodTube(UUID.randomUUID(),
                "Serum Red Tube 5ml", 0.65, 400,
                "Used for LFT, RFT, Lipid profile. Contains clot activator.",
                c[2], DATE_FMT.parse("2025-08-20"),
                5.0, "Red", "Clot Activator");

        BloodTube fluorideGreyTube = new BloodTube(UUID.randomUUID(),
                "Fluoride Grey Tube 2ml", 0.70, 2,
                "Used for Glucose (FBS/RBS). Contains Sodium Fluoride/Potassium Oxalate.",
                c[2], DATE_FMT.parse("2026-01-10"),
                2.0, "Grey", "Sodium Fluoride/Potassium Oxalate");

        inventory.add(
                edtaPurpleTube.getBloodTubeKey(),
                edtaPurpleTube
        );
        inventory.add(
                sodiumCitrateBlueTube.getBloodTubeKey(),
                sodiumCitrateBlueTube
        );
        inventory.add(
                serumRedTube.getBloodTubeKey(),
                serumRedTube
        );
        inventory.add(
                fluorideGreyTube.getBloodTubeKey(),
                fluorideGreyTube
        );

        return inventory;
    }

    public static HashedDictionary<String, LabTest> createLabTests() throws ParseException {
        HashedDictionary<String, LabTest> labTests = new HashedDictionary<>();
        HashedDictionary<String, BloodTube> inventory = createBloodTubeInventory();
        Company[] c = createSampleCompanies(); // For referring lab
        BloodTube edtaTube = inventory.getValue("EDTA Purple Tube 4ml");
        BloodTube citrateTube = inventory.getValue("Citrate Blue Tube 2.7ml");
        BloodTube serumTube = inventory.getValue("Serum Red Tube 5ml");
        BloodTube fluorideTube = inventory.getValue("Fluoride Grey Tube 2ml");
        // LabTest 1: Full Blood Count (FBC)
        if (edtaTube != null) {
            // Corrected: Passing the String name of the blood tube
            LabTest fbc = new LabTest(UUID.randomUUID(), "Full Blood Count", 15.00,
                    "Assesses red blood cells, white blood cells, and platelets. Used for anemia, infection.", c[1], "FBC", false,
                    "No special preparation required.", edtaTube.getName());
            labTests.add(fbc.getName(), fbc);
        }
        // LabTest 2: Liver Function Test (LFT)
        if (serumTube != null) {
            LabTest lft = new LabTest(UUID.randomUUID(), "Liver Function Test", 30.00,
                    "Evaluates liver health, checks enzymes and bilirubin. Aids in diagnosing liver diseases.", c[1], "LFT", true,
                    "Fasting 8-12 hours prior to test. No alcohol consumption.", serumTube.getName());
            labTests.add(lft.getName(), lft);
        }
        // LabTest 3: Coagulation Profile (PT/APTT)
        if (citrateTube != null) {
            LabTest coagulation = new LabTest(UUID.randomUUID(), "Coagulation Profile (PT/APTT)", 25.00,
                    "Checks blood's ability to clot. Used before surgery or for bleeding disorders.", c[1], "COAG", false,
                    "Inform doctor if on blood thinners.", citrateTube.getName());
            labTests.add(coagulation.getName(), coagulation);
        }
        // LabTest 4: Fasting Blood Glucose (FBS)
        if (fluorideTube != null) {
            LabTest glucose = new LabTest(UUID.randomUUID(), "Fasting Blood Glucose", 10.00,
                    "Measures blood sugar level after a period of fasting. Screens for diabetes.", c[1], "FBS", true,
                    "Fasting for at least 8 hours (water allowed).", fluorideTube.getName());
            labTests.add(glucose.getName(), glucose);
        }
        // Add more LabTest as needed
        return labTests;
    }

    /**
     * Public utility method.
     * Builds and returns a HashDictionary keyed by MedicineKey.
     */
    public static HashedDictionary<String, Medicine> createMedicineTable()
            throws ParseException {
        HashedDictionary<String, Medicine> table = new HashedDictionary<>();
        for (Medicine m : createSampleMedicines()) {
            table.add(m.getMedicineKey(), m);
        }
        return table;
    }
}
