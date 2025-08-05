package utility;

import adt.HashedDictionary;
import entity.pharmacyManagement.Company;
import entity.pharmacyManagement.Medicine;
import entity.pharmacyManagement.MedicineKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class GenerateMedicineData {
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
                        "Paracetamol", 3.50,
                        "Pain relief and fever reducer", "tablet",
                        c[0],  // Pfizer
                        "Panadol", "500 mg", "tablet",
                        DATE_FMT.parse("2026-12-31")),

                new Medicine(UUID.randomUUID(),
                        "Ibuprofen", 4.20,
                        "NSAID for pain and inflammation", "tablet",
                        c[1],  // GSK
                        "Advil", "200 mg", "tablet",
                        DATE_FMT.parse("2025-11-30")),

                new Medicine(UUID.randomUUID(),
                        "Amoxicillin", 12.00,
                        "Broad-spectrum antibiotic", "capsule",
                        c[2],  // Novartis
                        "Amoxil", "500 mg", "capsule",
                        DATE_FMT.parse("2026-06-30")),

                new Medicine(UUID.randomUUID(),
                        "Cetirizine", 2.75,
                        "Antihistamine for allergy relief", "tablet",
                        c[3],  // Roche
                        "Zyrtec", "10 mg", "tablet",
                        DATE_FMT.parse("2027-01-15")),

                new Medicine(UUID.randomUUID(),
                        "Metformin", 8.50,
                        "Type 2 diabetes management", "tablet",
                        c[4],  // J&J
                        "Glucophage", "500 mg", "tablet",
                        DATE_FMT.parse("2026-03-31")),

                new Medicine(UUID.randomUUID(),
                        "Omeprazole", 9.00,
                        "Proton-pump inhibitor for GERD", "capsule",
                        c[0],  // Pfizer
                        "Prilosec", "20 mg", "capsule",
                        DATE_FMT.parse("2026-09-30")),

                new Medicine(UUID.randomUUID(),
                        "Atorvastatin", 15.00,
                        "Cholesterol-lowering statin", "tablet",
                        c[1],  // GSK
                        "Lipitor", "10 mg", "tablet",
                        DATE_FMT.parse("2027-05-31"))
        };
    }

    /**
     * Public utility method.
     * Builds and returns a HashDictionary keyed by MedicineKey.
     */
    public static HashedDictionary<MedicineKey, Medicine> createMedicineTable()
            throws ParseException {
        HashedDictionary<MedicineKey, Medicine> table = new HashedDictionary<>();
        for (Medicine m : createSampleMedicines()) {
            table.add(m.getKey(), m);
        }
        return table;
    }
}
