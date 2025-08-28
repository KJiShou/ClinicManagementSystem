// Kong Ji Shou
package utility;

import adt.ArrayList;
import adt.Entry;
import adt.HashedDictionary;
import entity.pharmacyManagement.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.function.BiConsumer;

public class GeneratePharmacyData {
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");

    /** Predefined the company and medicine data */
    private static Company[] createSampleCompanies() {
        return new Company[] {
                // 0..4 keep your originals (Pfizer, GSK, Novartis, Roche, J&J)
                new Company(UUID.randomUUID(), "Pfizer", "235 East 42nd St, New York, NY", "info@pfizer.com", "1-800-555-0100"),
                new Company(UUID.randomUUID(), "GlaxoSmithKline (GSK)", "980 Great West Rd, Brentford, UK", "contact@gsk.com", "+44-20-8047-5000"),
                new Company(UUID.randomUUID(), "Novartis", "Lichtstrasse 35, 4056 Basel, Switzerland", "service@novartis.com", "+41-61-324-1111"),
                new Company(UUID.randomUUID(), "Roche", "Grenzacherstrasse 124, Basel, Switzerland", "contactus@roche.com", "+41-61-688-1111"),
                new Company(UUID.randomUUID(), "Johnson & Johnson", "One Johnson & Johnson Plaza, New Brunswick, NJ", "info@jnj.com", "1-800-555-0155"),

                // Added vendors commonly appearing in the PDF
                new Company(UUID.randomUUID(), "Hovid", "121, Jalan Tunku Abdul Rahman, 30010 Ipoh, Perak", "info@hovid.com", "+60-5-506-7788"),
                new Company(UUID.randomUUID(), "Xepa-Soul Pattinson (Xepa)", "Batu Kawan Industrial Park, Penang", "info@xepasp.com", "+60-4-646-2828"),
                new Company(UUID.randomUUID(), "Pharmaniaga", "No. 7, Persiaran Perindustrian, Shah Alam", "enquiry@pharmaniaga.com", "+60-3-3342-9999"),
                new Company(UUID.randomUUID(), "Zuellig Pharma", "Bangsar South, Kuala Lumpur", "info@zuellig.com", "+60-3-2287-9888"),
                new Company(UUID.randomUUID(), "First Pharma", "Klang Valley", "hello@firstpharma.my", "+60-3-0000-0000"),
                new Company(UUID.randomUUID(), "CCM (Duopharma/CCM)", "Shah Alam", "info@duopharma.com", "+60-3-5519-4450"),
                new Company(UUID.randomUUID(), "Himalaya", "Bangalore, India", "care@himalayawellness.com", "+91-80-4016-0000"),
                new Company(UUID.randomUUID(), "Ranbaxy (Sun Pharma)", "Mumbai, India", "info@sunpharma.com", "+91-22-4324-4324"),
                new Company(UUID.randomUUID(), "Medispec", "Penang", "info@medispec.com.my", "+60-4-282-3877"),
                new Company(UUID.randomUUID(), "Sandoz", "Basel, Switzerland", "info@sandoz.com", "+41-61-324-1111"),
        };
    }

    private static Medicine[] createSampleMedicines() throws ParseException {
        Company[] c = createSampleCompanies();
        // For readability
        final Date EXP_2027 = DATE_FMT.parse("2027-12-31");
        final Date EXP_2026 = DATE_FMT.parse("2026-12-31");

        return new Medicine[]{
                // ===== Analgesic / Antipyretic / NSAID =====
                new Medicine(UUID.randomUUID(), "Paracetamol", 10, 6.50,  // Panadol 500 mg 10s typical RM ~6–7
                        "Analgesic/antipyretic", "tablet",
                        c[0], "Panadol", "500 mg", EXP_2027),  // PDF OTC lines including PCM/Adamol 500 mg :contentReference[oaicite:0]{index=0}

                new Medicine(UUID.randomUUID(), "Paracetamol", 10, 4.50,
                        "Analgesic/antipyretic (economy)", "tablet",
                        c[5], "Adamol (Hovid)", "500 mg", EXP_2027),  // Adamol Hovid 500 mg appears in PDF set :contentReference[oaicite:1]{index=1}

                new Medicine(UUID.randomUUID(), "Paracetamol", 10, 8.90,
                        "Higher strength PCM", "tablet",
                        c[0], "Paracetamol", "650 mg", EXP_2027),  // PCM 650 mg listed in PDF roundup :contentReference[oaicite:2]{index=2}

                new Medicine(UUID.randomUUID(), "Ibuprofen", 10, 9.90,
                        "NSAID for pain and inflammation", "tablet",
                        c[1], "Ibuprofen (YSP)", "400 mg", EXP_2027),  // Ibuprofen YSP 400 mg :contentReference[oaicite:3]{index=3}

                new Medicine(UUID.randomUUID(), "Mefenamic Acid", 10, 12.90,
                        "NSAID (dysmenorrhea, dental pain)", "tablet",
                        c[1], "Ponstan", "500 mg", EXP_2027),  // Ponstan mentioned :contentReference[oaicite:4]{index=4}

                new Medicine(UUID.randomUUID(), "Maxigesic (Paracetamol+Ibuprofen)", 10, 18.90,
                        "Combo analgesic", "tablet",
                        c[2], "Maxigesic", "500 mg/150 mg", EXP_2027),  // Maxigesic 500/150 :contentReference[oaicite:5]{index=5}

                new Medicine(UUID.randomUUID(), "Etoricoxib", 10, 24.90,
                        "COX-2 selective NSAID", "tablet",
                        c[5], "Hovid Etoricoxib", "120 mg", EXP_2027),  // Etoricoxib 120 mg Hovid :contentReference[oaicite:6]{index=6}

                new Medicine(UUID.randomUUID(), "Celecoxib", 10, 22.00,
                        "COX-2 inhibitor", "capsule",
                        c[2], "Torcoxib / Celecoxib (generic)", "200 mg", EXP_2027),  // Celecoxib 200 mg generic :contentReference[oaicite:7]{index=7}

                new Medicine(UUID.randomUUID(), "Naproxen Sodium", 10, 16.90,
                        "NSAID", "tablet",
                        c[2], "Sunprox / Synflex", "550 mg", EXP_2027),  // SUNPROX 550 / Synflex 550 :contentReference[oaicite:8]{index=8}

                // ===== Antihistamines / Allergy =====
                new Medicine(UUID.randomUUID(), "Bilastine", 10, 28.00,
                        "Antihistamine", "tablet",
                        c[2], "Bilaxten", "20 mg", EXP_2027),  // Bilaxten 20 mg (adult) & kids 10 mg ODT :contentReference[oaicite:9]{index=9}

                new Medicine(UUID.randomUUID(), "Chlorpheniramine", 10, 5.50,
                        "First-gen antihistamine (sedating)", "tablet",
                        c[11], "Piriton / Allersin-F", "4 mg", EXP_2027),  // Piriton/Allersin-F 4 mg :contentReference[oaicite:10]{index=10}

                new Medicine(UUID.randomUUID(), "Cetirizine", 10, 7.90,
                        "Non-sedating antihistamine", "tablet",
                        c[2], "Cetirizine", "10 mg", EXP_2027),  // Cetirizine 10 mg :contentReference[oaicite:11]{index=11}

                new Medicine(UUID.randomUUID(), "Loratadine", 10, 7.90,
                        "Non-sedating antihistamine", "tablet",
                        c[2], "Loratadine / Lorazin", "10 mg", EXP_2027),  // Loratadine/Lorazin 10 mg :contentReference[oaicite:12]{index=12}

                new Medicine(UUID.randomUUID(), "Levocetirizine", 10, 11.90,
                        "Non-sedating antihistamine", "tablet",
                        c[2], "Avozine / Xyzal (Xepa)", "5 mg", EXP_2027),  // Levocetirizine 5 mg (Avozine/Xyzal) :contentReference[oaicite:13]{index=13}

                // ===== Cough / Throat / Phlegm =====
                new Medicine(UUID.randomUUID(), "Ambroxol", 10, 12.50,
                        "Mucolytic", "tablet",
                        c[2], "Bisolvon / Ambroxol", "30 mg", EXP_2027),  // Ambroxol 30 mg, Bisolvon 8 mg lines :contentReference[oaicite:14]{index=14}

                new Medicine(UUID.randomUUID(), "Acetylcysteine", 10, 19.90,
                        "Mucolytic (effervescent)", "tablet",
                        c[2], "Stacytine", "600 mg", EXP_2027),  // Stacytine 600 mg effervescent :contentReference[oaicite:15]{index=15}

                new Medicine(UUID.randomUUID(), "Acetylcysteine (kids)", 10, 23.90,
                        "Mucolytic (kids sachet)", "sachet",
                        c[2], "Fluimucil", "200 mg", EXP_2027),  // FLUIMUCIL 200 kids sachet :contentReference[oaicite:16]{index=16}

                new Medicine(UUID.randomUUID(), "Dextromethorphan + combo", 1, 15.90,
                        "Cough linctus (DM+others)", "syrup",
                        c[7], "Sedilix-DM / Tussidex Forte / Pabron", "—", EXP_2027),  // Sedilix, Tussidex, Pabron list :contentReference[oaicite:17]{index=17}

                new Medicine(UUID.randomUUID(), "Benzydamine Throat Spray", 1, 24.90,
                        "Anti-inflammatory throat spray", "spray",
                        c[2], "Difflam Forte 0.3% w/v", "—", EXP_2027),  // Difflam throat spray 0.3% :contentReference[oaicite:18]{index=18}

                new Medicine(UUID.randomUUID(), "Difflam Lozenges", 1, 14.90,
                        "Anti-inflammatory lozenges", "lozenge",
                        c[2], "Difflam Mint", "3 mg", EXP_2027),  // Difflam lozenges 3 mg :contentReference[oaicite:19]{index=19}

                // ===== GI (antiemetic / PPI / antispasmodic / diarrhea) =====
                new Medicine(UUID.randomUUID(), "Metoclopramide", 10, 8.90,
                        "Antiemetic", "tablet",
                        c[2], "Maxalon / Pulin", "10 mg", EXP_2027),  // Maxalon/Metoclopramide lines :contentReference[oaicite:20]{index=20}

                new Medicine(UUID.randomUUID(), "Dimenhydrinate", 10, 9.90,
                        "Antiemetic/anti-vertigo", "tablet",
                        c[2], "Gravol / Novomin", "50 mg", EXP_2027),  // Gravol/Dimenhydrinate 50 mg :contentReference[oaicite:21]{index=21}

                new Medicine(UUID.randomUUID(), "Prochlorperazine", 10, 12.90,
                        "Antiemetic", "tablet",
                        c[2], "Stemetil / Properazine", "5 mg", EXP_2027),  // Stemetil/Properazine 5 mg :contentReference[oaicite:22]{index=22}

                new Medicine(UUID.randomUUID(), "Omeprazole", 10, 18.90,
                        "PPI for GERD", "capsule",
                        c[0], "Probitor (Sandoz)/Omeprazole", "20 mg", EXP_2027),  // Probitor 20 mg capsules :contentReference[oaicite:23]{index=23}

                new Medicine(UUID.randomUUID(), "Pantoprazole", 10, 19.90,
                        "PPI for GERD", "tablet",
                        c[2], "Pantoprazole (Pantor/Sandoz)", "40 mg", EXP_2027),  // Pantopraole Sandox 40 mg / Pantor :contentReference[oaicite:24]{index=24}

                new Medicine(UUID.randomUUID(), "Itopride", 10, 21.90,
                        "Prokinetic", "tablet",
                        c[2], "Ganaton", "50 mg", EXP_2027),  // Ganaton listed :contentReference[oaicite:25]{index=25}

                new Medicine(UUID.randomUUID(), "Hyoscine Butylbromide", 10, 11.90,
                        "Antispasmodic", "tablet",
                        c[2], "Buscopan", "10 mg", EXP_2027),  // Buscopan listed :contentReference[oaicite:26]{index=26}

                new Medicine(UUID.randomUUID(), "Activated Charcoal", 10, 7.90,
                        "Adsorbent for GI upset", "tablet",
                        c[2], "Ultracarbon", "250 mg", EXP_2027),  // Charcoal line :contentReference[oaicite:27]{index=27}

                new Medicine(UUID.randomUUID(), "Loperamide", 10, 8.90,
                        "Anti-diarrheal", "capsule",
                        c[2], "Lomide", "2 mg", EXP_2027),  // Lomide 2 mg :contentReference[oaicite:28]{index=28}

                new Medicine(UUID.randomUUID(), "ORS", 1, 4.90,
                        "Oral rehydration salts", "sachet",
                        c[2], "ORS Plus", "—", EXP_2027),  // ORS Plus :contentReference[oaicite:29]{index=29}

                new Medicine(UUID.randomUUID(), "Smecta (Diosmectite)", 10, 21.90,
                        "Anti-diarrheal", "sachet",
                        c[2], "Smecta", "3 g", EXP_2027),  // Smecta 3 g :contentReference[oaicite:30]{index=30}

                new Medicine(UUID.randomUUID(), "Lactulose", 1, 16.90,
                        "Osmotic laxative", "syrup",
                        c[2], "Osmolax Lactulose", "0.68 g/ml 100 ml", EXP_2027),  // Osmolax lactulose 100 ml :contentReference[oaicite:31]{index=31}

                // ===== Respiratory (asthma/rhinitis) =====
                new Medicine(UUID.randomUUID(), "Montelukast", 10, 22.90,
                        "Leukotriene receptor antagonist", "tablet",
                        c[8], "Aspira / Asthator", "10 mg", EXP_2027),  // Montelukast adult/kids (Aspira/OXAIR/SALBUTIS etc.) :contentReference[oaicite:32]{index=32}

                new Medicine(UUID.randomUUID(), "Fluticasone (nasal)", 1, 32.00,
                        "Allergic rhinitis nasal spray", "spray",
                        c[1], "Flutinide / Avamys", "50 mcg/dose", EXP_2027),  // Fluticasone nasal (context list) :contentReference[oaicite:33]{index=33}

                // ===== Antibiotics (oral) =====
                new Medicine(UUID.randomUUID(), "Doxycycline", 10, 18.00,
                        "Tetracycline-class antibiotic", "capsule",
                        c[2], "Doxycycline", "100 mg", EXP_2026),  // Doxycycline lines :contentReference[oaicite:34]{index=34}

                new Medicine(UUID.randomUUID(), "Azithromycin", 6, 28.00,
                        "Macrolide antibiotic", "tablet",
                        c[0], "Zithromax", "250 mg", EXP_2026),  // Azith 250 / Syrup 200 mg/5 ml Original Pfizer :contentReference[oaicite:35]{index=35}

                new Medicine(UUID.randomUUID(), "Cefuroxime Axetil", 10, 48.00,
                        "2nd-gen cephalosporin", "tablet",
                        c[9], "Cefuroxime (Medispec)", "250 mg", EXP_2026),  // Cefuroxime Medispec 250 mg :contentReference[oaicite:36]{index=36}

                new Medicine(UUID.randomUUID(), "Amoxicillin", 10, 14.90,
                        "Penicillin-class antibiotic", "capsule",
                        c[12], "Synamox / YSP", "500 mg", EXP_2026),  // Amoxicillin 500 mg SYNAMOX / YSP :contentReference[oaicite:37]{index=37}

                new Medicine(UUID.randomUUID(), "Clarithromycin", 10, 58.00,
                        "Macrolide antibiotic", "tablet",
                        c[2], "Clarithromycin (Stella)", "500 mg", EXP_2026),  // Clarithromycin Stella 500 mg :contentReference[oaicite:38]{index=38}

                new Medicine(UUID.randomUUID(), "Levofloxacin", 10, 65.00,
                        "Fluoroquinolone antibiotic", "tablet",
                        c[2], "Locikline (Levofloxacin)", "500 mg", EXP_2026),  // Locikline levofloxacin 500 mg :contentReference[oaicite:39]{index=39}

                new Medicine(UUID.randomUUID(), "Co-Amoxiclav (Augmentin/Curam)", 14, 85.00,
                        "Amoxicillin + clavulanate", "tablet",
                        c[1], "Augmentin / Curam / Cavumox", "625 mg", EXP_2026),  // Curam 625 / Co-amoxiclav 625 mg :contentReference[oaicite:40]{index=40}

                // ===== Pediatric suspensions from PDF =====
                new Medicine(UUID.randomUUID(), "Co-Amoxiclav (susp.)", 1, 45.00,
                        "Pediatric co-amoxiclav", "syrup",
                        c[1], "Augmentin", "457 mg/5 ml", EXP_2026),  // Augmentin 457 mg/5 ml GSK :contentReference[oaicite:41]{index=41}

                new Medicine(UUID.randomUUID(), "Azithromycin (susp.)", 1, 55.00,
                        "Pediatric macrolide", "syrup",
                        c[0], "Zithromax", "200 mg/5 ml", EXP_2026),  // Zithromax 200 mg/5 ml Original Pfizer :contentReference[oaicite:42]{index=42}

                new Medicine(UUID.randomUUID(), "Cefaclor (susp.)", 1, 38.00,
                        "Pediatric cephalosporin", "syrup",
                        c[2], "Sifaclor", "250 mg/5 ml", EXP_2026),  // Sifaclor 250 mg/5 ml 60 ml :contentReference[oaicite:43]{index=43}

                // ===== Vertigo / Neuro / Steroid / Muscle relaxant =====
                new Medicine(UUID.randomUUID(), "Cinnarizine", 10, 9.90,
                        "Anti-vertigo", "tablet",
                        c[1], "Stugeron", "25 mg", EXP_2027),  // Cinnarizine 25 mg (context) :contentReference[oaicite:44]{index=44}

                new Medicine(UUID.randomUUID(), "Betahistine", 10, 22.00,
                        "Vertigo (Menière)", "tablet",
                        c[2], "Betaserc", "24 mg", EXP_2027),  // Betaserc listed :contentReference[oaicite:45]{index=45}

                new Medicine(UUID.randomUUID(), "Eperisone", 10, 28.00,
                        "Muscle relaxant", "tablet",
                        c[2], "Myonal", "50 mg", EXP_2027),  // Myonal 50 mg :contentReference[oaicite:46]{index=46}

                new Medicine(UUID.randomUUID(), "Mecobalamin (B12)", 10, 18.00,
                        "Neuropathic support", "tablet",
                        c[2], "Methycobalt", "500 mcg", EXP_2027),  // Methycobalt (Mecobalamin) 500 mcg :contentReference[oaicite:47]{index=47}

                new Medicine(UUID.randomUUID(), "Prednisolone", 10, 6.50,
                        "Corticosteroid", "tablet",
                        c[2], "Prednisolone", "5 mg", EXP_2027),  // Prednisolone line :contentReference[oaicite:48]{index=48}

                new Medicine(UUID.randomUUID(), "Dexamethasone", 10, 7.50,
                        "Corticosteroid", "tablet",
                        c[3], "Dexa (CCM)", "0.75 mg", EXP_2027),  // Dexa 0.75 mg (CCM) :contentReference[oaicite:49]{index=49}

                // ===== Chronic: HTN / DM / Lipids =====
                new Medicine(UUID.randomUUID(), "Amlodipine/Valsartan", 28, 75.00,
                        "Antihypertensive combo", "tablet",
                        c[2], "Amval / Exforge (generic)", "10 mg/160 mg", EXP_2027),  // Amval Exforge Generic 10/160 mg :contentReference[oaicite:50]{index=50}

                new Medicine(UUID.randomUUID(), "Amlodipine/Valsartan/HCTZ", 28, 95.00,
                        "Antihypertensive triple combo", "tablet",
                        c[2], "Exforge HCT", "10/160/12.5 mg", EXP_2027),  // Exforge 10/160/12.5 and 10/160/25 :contentReference[oaicite:51]{index=51}

                new Medicine(UUID.randomUUID(), "Metformin", 30, 9.90,
                        "Biguanide (DM2)", "tablet",
                        c[12], "Glumet", "500 mg", EXP_2027),  // Glumet referenced in list :contentReference[oaicite:52]{index=52}

                new Medicine(UUID.randomUUID(), "Vildagliptin/Metformin", 28, 95.00,
                        "DPP-4 + biguanide", "tablet",
                        c[2], "Galvus Met", "50/1000 mg", EXP_2027),  // Galvusmet 50/1000 mg :contentReference[oaicite:53]{index=53}

                new Medicine(UUID.randomUUID(), "Dapagliflozin/Metformin XR", 28, 120.00,
                        "SGLT2 + biguanide", "tablet",
                        c[2], "Xigduo XR", "5/1000 mg", EXP_2027),  // Xigduo XR 5/1000 & 10/1000 mg :contentReference[oaicite:54]{index=54}

                new Medicine(UUID.randomUUID(), "Rosuvastatin", 28, 45.00,
                        "Statin", "tablet",
                        c[2], "Novtor", "20 mg", EXP_2027),  // Novtor Rosuvastatin 20 mg :contentReference[oaicite:55]{index=55}

                new Medicine(UUID.randomUUID(), "Atorvastatin", 28, 38.00,
                        "Statin", "tablet",
                        c[11], "Atorvastatin (CCM)", "20 mg", EXP_2027),  // Atorvastatin CCM 20 mg :contentReference[oaicite:56]{index=56}

                // ===== Gout / Uric Acid / Bleeding =====
                new Medicine(UUID.randomUUID(), "Allopurinol", 28, 12.90,
                        "Xanthine oxidase inhibitor", "tablet",
                        c[2], "Allopurinol", "300 mg", EXP_2027),  // Allopurinol 300 mg :contentReference[oaicite:57]{index=57}

                new Medicine(UUID.randomUUID(), "Colchicine", 28, 16.90,
                        "Gout acute management", "tablet",
                        c[2], "Colchicine", "0.6 mg", EXP_2027),  // Colchicine 0.6 mg :contentReference[oaicite:58]{index=58}

                new Medicine(UUID.randomUUID(), "Tranexamic Acid", 10, 19.90,
                        "Antifibrinolytic", "capsule",
                        c[10], "Transamin (First Pharma)", "250 mg", EXP_2027),  // Transamin / Tranexamic Acid 250 mg First Pharma :contentReference[oaicite:59]{index=59}

                // ===== Antifungal / Antiviral =====
                new Medicine(UUID.randomUUID(), "Itraconazole", 10, 48.00,
                        "Systemic antifungal", "capsule",
                        c[5], "Hovid Inox", "100 mg", EXP_2027),  // Hovid inox itraconazole 100 mg :contentReference[oaicite:60]{index=60}

                new Medicine(UUID.randomUUID(), "Terbinafine", 10, 38.00,
                        "Systemic antifungal", "tablet",
                        c[2], "Apo-Terbinafine", "250 mg", EXP_2027),  // Apo-Terbinafine 250 mg :contentReference[oaicite:61]{index=61}

                new Medicine(UUID.randomUUID(), "Fluconazole", 2, 26.00,
                        "Antifungal (single-dose)", "tablet",
                        c[8], "Pharmaniaga Fluconazole / Difluvid", "150 mg", EXP_2027),  // Pharmaniaga Fluconazole 150 mg :contentReference[oaicite:62]{index=62}

                new Medicine(UUID.randomUUID(), "Acyclovir", 30, 29.00,
                        "Antiviral (HSV/VZV)", "tablet",
                        c[13], "Lovir / Acyclovir (Ranbaxy)", "400 mg", EXP_2027),  // Ranbaxy Lovir aciclovir 400 mg :contentReference[oaicite:63]{index=63}

                // ===== UTI / GU / Thyroid / ENT / Eye / Ear =====
                new Medicine(UUID.randomUUID(), "Ural", 10, 18.90,
                        "Urinary alkalinizer", "sachet",
                        c[2], "Ural Effervescent Granules", "—", EXP_2027),  // Ural effervescent granules :contentReference[oaicite:64]{index=64}

                new Medicine(UUID.randomUUID(), "Fosfomycin Trometamol", 1, 58.00,
                        "Single-dose UTI antibiotic", "sachet",
                        c[2], "Monurol", "3 g", EXP_2027),  // Monurol 3 g granules :contentReference[oaicite:65]{index=65}

                new Medicine(UUID.randomUUID(), "Carbimazole", 50, 36.00,
                        "Antithyroid", "tablet",
                        c[2], "Camazol", "5 mg", EXP_2027),  // Camazol carbimazole 5 mg :contentReference[oaicite:66]{index=66}

                new Medicine(UUID.randomUUID(), "Clotrimazole V1", 1, 19.90,
                        "Vaginal insert (1-day)", "pessary",
                        c[2], "Candid V1", "—", EXP_2027),  // Canoio V1 clotrimazole vaginal insert :contentReference[oaicite:67]{index=67}

                new Medicine(UUID.randomUUID(), "Clotrimazole V3", 1, 24.90,
                        "Vaginal insert (3-day)", "pessary",
                        c[2], "Candid V3", "200 mg", EXP_2027),  // Canoio V3 200 mg :contentReference[oaicite:68]{index=68}

                new Medicine(UUID.randomUUID(), "Neo-Penotran", 1, 42.00,
                        "Metronidazole + Miconazole", "pessary",
                        c[2], "Neo-Penotran", "—", EXP_2027),  // Neo-pentran vaginal pessary :contentReference[oaicite:69]{index=69}

                new Medicine(UUID.randomUUID(), "Ciprofloxacin (eye)", 1, 18.00,
                        "Antibacterial eye drops", "drop",
                        c[2], "CIPMAX", "0.3% 5 ml", EXP_2027),  // CIPMAX eye drops 0.3% :contentReference[oaicite:70]{index=70}

                new Medicine(UUID.randomUUID(), "Dexa-Gentamicin (eye)", 1, 16.00,
                        "Antibiotic + steroid", "drop",
                        c[2], "Dexa-Gentamicin", "5 ml", EXP_2027),  // Dexa-Gentamicin 5 ml :contentReference[oaicite:71]{index=71}

                new Medicine(UUID.randomUUID(), "Artificial Tears", 1, 12.00,
                        "Lubricant", "drop",
                        c[2], "Lacri-Vision", "—", EXP_2027),  // Lacri-Vision :contentReference[oaicite:72]{index=72}

                new Medicine(UUID.randomUUID(), "Ofloxacin (ear)", 1, 18.00,
                        "Antibacterial eardrops", "drop",
                        c[2], "POCIN-H", "5 ml", EXP_2027),  // Ear: POCIN-H (context) :contentReference[oaicite:73]{index=73}
        };
    }

    public static HashedDictionary<String, BloodTube> createBloodTubeInventory() throws ParseException {
        HashedDictionary<String, BloodTube> inventory = new HashedDictionary<>();
        Company[] c = createSampleCompanies();

        BloodTube edtaPurpleTube = new BloodTube(UUID.randomUUID(),
                "EDTA Purple Tube",
                0.75, 500,
                "Used for CBC, HbA1c. Contains EDTA anticoagulant",
                c[2],
                DATE_FMT.parse("2026-01-01"),
                4.0, "Purple", "EDTA K2");

        BloodTube sodiumCitrateBlueTube = new BloodTube(UUID.randomUUID(),
                "Sodium Citrate Blue Tube",
                0.75, 300,
                "Used for Coagulation profile (PT/APTT). Contains Sodium Citrate.",
                c[2], DATE_FMT.parse("2025-09-10"),
                2.7, "Blue", "Sodium Citrate");

        BloodTube serumRedTube = new BloodTube(UUID.randomUUID(),
                "Serum Red Tube", 0.65, 400,
                "Used for LFT, RFT, Lipid profile. Contains clot activator.",
                c[2], DATE_FMT.parse("2025-08-20"),
                5.0, "Red", "Clot Activator");

        BloodTube fluorideGreyTube = new BloodTube(UUID.randomUUID(),
                "Fluoride Grey Tube", 0.70, 200,
                "Used for Glucose (FBS/RBS). Contains Sodium Fluoride/Potassium Oxalate.",
                c[2], DATE_FMT.parse("2026-01-10"),
                2.0, "Grey", "Sodium Fluoride/Potassium Oxalate");

        // NEW: Serum Separator Tube (SST) — Gold cap
        BloodTube sstGoldTube = new BloodTube(UUID.randomUUID(),
                "SST Gold Tube", 0.80, 300,
                "Serum separator with gel; preferred for chemistries, hormones, tumor markers.",
                c[2], DATE_FMT.parse("2026-03-01"),
                5.0, "Gold", "Clot Activator + Gel");

        // NEW: Lithium Heparin — Green cap (plasma chemistry/electrolytes)
        BloodTube lithiumHeparinGreenTube = new BloodTube(UUID.randomUUID(),
                "Lithium Heparin Green Tube", 0.75, 200,
                "Plasma chemistry/electrolytes; fast TAT without clotting delay.",
                c[2], DATE_FMT.parse("2026-04-01"),
                4.0, "Green", "Lithium Heparin");

        // NEW: ESR — Black cap (3.8% sodium citrate)
        BloodTube esrBlackTube = new BloodTube(UUID.randomUUID(),
                "ESR Black Tube", 0.70, 150,
                "Dedicated ESR (Westergren) tube; 3.8% sodium citrate.",
                c[2], DATE_FMT.parse("2026-06-01"),
                1.6, "Black", "Sodium Citrate 3.8%");

        // Add all to inventory
        inventory.add(edtaPurpleTube.getBloodTubeKey(), edtaPurpleTube);
        inventory.add(sodiumCitrateBlueTube.getBloodTubeKey(), sodiumCitrateBlueTube);
        inventory.add(serumRedTube.getBloodTubeKey(), serumRedTube);
        inventory.add(fluorideGreyTube.getBloodTubeKey(), fluorideGreyTube);
        inventory.add(sstGoldTube.getBloodTubeKey(), sstGoldTube);
        inventory.add(lithiumHeparinGreenTube.getBloodTubeKey(), lithiumHeparinGreenTube);
        inventory.add(esrBlackTube.getBloodTubeKey(), esrBlackTube);

        return inventory;
    }

    public static HashedDictionary<String, LabTest> createLabTests() throws ParseException {
        HashedDictionary<String, LabTest> labTests = new HashedDictionary<>();
        Company[] c = createSampleCompanies();
        Company refLab = c[1];

        BiConsumer<String, LabTest> put = labTests::add;

        // ---------- Hematology ----------
        put.accept("Full Blood Count", new LabTest(
                UUID.randomUUID(), "Full Blood Count", 18.00,
                "RBC/WBC/Platelets; anemia/infection screen.",
                refLab, "FBC", false,
                "No special preparation.", "EDTA Purple Tube"));

        put.accept("ESR", new LabTest(
                UUID.randomUUID(), "Erythrocyte Sedimentation Rate", 15.00,
                "Inflammation marker (Westergren).",
                refLab, "ESR", false,
                "No special preparation.", "ESR Black Tube"));

        put.accept("Coagulation Profile (PT/APTT)", new LabTest(
                UUID.randomUUID(), "Coagulation Profile (PT/APTT)", 28.00,
                "Bleeding/clotting screen; pre-op assessment.",
                refLab, "PT/APTT", false,
                "Inform if on anticoagulants.", "Sodium Citrate Blue Tube"));

        put.accept("ABO + Rh Blood Group", new LabTest(
                UUID.randomUUID(), "ABO + Rh Blood Group", 20.00,
                "Determines ABO and RhD blood type.",
                refLab, "ABORH", false,
                "No special preparation.", "EDTA Purple Tube"));

        put.accept("High Sensitive CRP", new LabTest(
                UUID.randomUUID(), "High Sensitive CRP", 35.00,
                "Cardiovascular risk / low-grade inflammation.",
                refLab, "hsCRP", false,
                "No special preparation.", "SST Gold Tube"));

        put.accept("RA Factor", new LabTest(
                UUID.randomUUID(), "Rheumatoid Factor", 30.00,
                "Autoimmune screen for rheumatoid arthritis.",
                refLab, "RF", false,
                "No special preparation.", "SST Gold Tube"));

        // ---------- Diabetes ----------
        put.accept("Fasting Blood Glucose", new LabTest(
                UUID.randomUUID(), "Fasting Blood Glucose", 12.00,
                "Screens diabetes; fasting plasma glucose.",
                refLab, "FBG", true,
                "Fast 8–10 hours; water allowed.", "Fluoride Grey Tube"));

        put.accept("HbA1c", new LabTest(
                UUID.randomUUID(), "HbA1c", 35.00,
                "3-month average glucose control.",
                refLab, "HbA1c", false,
                "No special preparation.", "EDTA Purple Tube"));

        put.accept("Diabetic Study (FBG + HbA1c)", new LabTest(
                UUID.randomUUID(), "Diabetic Study (FBG + HbA1c)", 45.00,
                "Combined screening and control assessment.",
                refLab, "DM-STUDY", true,
                "Fast 8–10 hours; water allowed.", "Fluoride Grey Tube + EDTA Purple Tube"));

        // ---------- Renal / Liver / Lipid / Iron ----------
        put.accept("Renal Function Test", new LabTest(
                UUID.randomUUID(), "Renal Function Test", 28.00,
                "Urea/creatinine/electrolytes; eGFR.",
                refLab, "RFT", false,
                "Well hydrated; avoid heavy exercise.", "SST Gold Tube"));

        put.accept("Liver Function Test", new LabTest(
                UUID.randomUUID(), "Liver Function Test", 30.00,
                "AST, ALT, ALP, GGT, bilirubin, albumin.",
                refLab, "LFT", false,
                "Avoid alcohol 24h before sample.", "SST Gold Tube"));

        put.accept("Lipid Profile", new LabTest(
                UUID.randomUUID(), "Lipid Profile", 32.00,
                "TC, LDL, HDL, TG.",
                refLab, "LIPID", true,
                "Fast 8–10 hours; water allowed.", "SST Gold Tube"));

        put.accept("Serum Uric Acid", new LabTest(
                UUID.randomUUID(), "Serum Uric Acid", 15.00,
                "Gout risk and therapy monitoring.",
                refLab, "UA", false,
                "Avoid high-purine meal the night before.", "SST Gold Tube"));

        put.accept("Serum Ferritin", new LabTest(
                UUID.randomUUID(), "Serum Ferritin", 35.00,
                "Iron stores; anemia evaluation.",
                refLab, "FERRITIN", false,
                "No special preparation.", "SST Gold Tube"));

        put.accept("Serum Iron", new LabTest(
                UUID.randomUUID(), "Serum Iron", 22.00,
                "Part of iron studies.",
                refLab, "IRON", true,
                "Morning sample preferred; fast 8h.", "SST Gold Tube"));

        // ---------- Urine ----------
        put.accept("Urine FEME", new LabTest(
                UUID.randomUUID(), "Urine FEME", 12.00,
                "Routine urinalysis (chemical + microscopic).",
                refLab, "FEME", false,
                "Midstream clean-catch preferred.", "N/A"));

        put.accept("Urine Microalbumin", new LabTest(
                UUID.randomUUID(), "Urine Microalbumin", 25.00,
                "Early kidney damage screen in DM/HT.",
                refLab, "UMALB", false,
                "First morning urine or random spot.", "N/A"));

        // ---------- Infectious Disease / Serology ----------
        put.accept("Hepatitis A Antibody", new LabTest(
                UUID.randomUUID(), "Hepatitis A Antibody", 30.00,
                "Immunity or past exposure to HAV.",
                refLab, "HAV-Ab", false,
                "No special preparation.", "SST Gold Tube"));

        put.accept("Hepatitis B Antigen & Antibody", new LabTest(
                UUID.randomUUID(), "Hepatitis B Antigen & Antibody", 38.00,
                "HBsAg & anti-HBs for infection/immunity.",
                refLab, "HBV", false,
                "No special preparation.", "SST Gold Tube"));

        put.accept("HIV 1 & 2 Antibody/Ag", new LabTest(
                UUID.randomUUID(), "HIV 1 & 2 Antibody/Ag", 45.00,
                "Screening for HIV infection.",
                refLab, "HIV1/2", false,
                "Consent required.", "SST Gold Tube"));

        put.accept("H. pylori Antibody", new LabTest(
                UUID.randomUUID(), "H. pylori Antibody", 35.00,
                "Screens prior exposure to H. pylori.",
                refLab, "HP-Ab", false,
                "No special preparation.", "SST Gold Tube"));

        put.accept("H. pylori Stool Antigen", new LabTest(
                UUID.randomUUID(), "H. pylori Stool Antigen", 45.00,
                "Detects active H. pylori infection.",
                refLab, "HP-Ag", false,
                "Avoid antibiotics/PPIs 2 weeks prior if possible.", "N/A"));

        put.accept("Serology (VDRL/TPHA)", new LabTest(
                UUID.randomUUID(), "Serology (VDRL/TPHA)", 30.00,
                "Syphilis screening panel.",
                refLab, "SEROLOGY", false,
                "No special preparation.", "SST Gold Tube"));

        // ---------- Thyroid / Hormones ----------
        put.accept("Thyroid Function (TSH + Free T4)", new LabTest(
                UUID.randomUUID(), "Thyroid Function (TSH + Free T4)", 48.00,
                "Evaluates hypo/hyper-thyroidism.",
                refLab, "TSH+FT4", false,
                "Morning sample preferred.", "SST Gold Tube"));

        put.accept("Estradiol (E2)", new LabTest(
                UUID.randomUUID(), "Estradiol (E2)", 45.00,
                "Female hormone; cycle/fertility assessment.",
                refLab, "E2", false,
                "Note cycle day.", "SST Gold Tube"));

        put.accept("FSH", new LabTest(
                UUID.randomUUID(), "FSH", 35.00,
                "Ovarian reserve / menopause; fertility workup.",
                refLab, "FSH", false,
                "Note cycle day.", "SST Gold Tube"));

        put.accept("LH", new LabTest(
                UUID.randomUUID(), "LH", 35.00,
                "Ovulation timing / PCOS workup.",
                refLab, "LH", false,
                "Note cycle day.", "SST Gold Tube"));

        put.accept("Prolactin", new LabTest(
                UUID.randomUUID(), "Prolactin", 35.00,
                "Hyperprolactinemia; pituitary assessment.",
                refLab, "PRL", false,
                "Avoid stress & heavy exercise.", "SST Gold Tube"));

        // ---------- Tumor Markers ----------
        put.accept("Alpha Fetoprotein (AFP)", new LabTest(
                UUID.randomUUID(), "Alpha Fetoprotein (AFP)", 40.00,
                "Hepatocellular carcinoma / germ cell tumors.",
                refLab, "AFP", false,
                "No special preparation.", "SST Gold Tube"));

        put.accept("CA 125 (Ovary Cancer Marker)", new LabTest(
                UUID.randomUUID(), "CA 125 (Ovary Cancer Marker)", 65.00,
                "Ovarian tumor marker (monitoring).",
                refLab, "CA125", false,
                "No special preparation.", "SST Gold Tube"));

        put.accept("CA 153 (Breast Cancer Marker)", new LabTest(
                UUID.randomUUID(), "CA 153 (Breast Cancer Marker)", 60.00,
                "Breast cancer marker (surveillance).",
                refLab, "CA153", false,
                "No special preparation.", "SST Gold Tube"));

        put.accept("CA 199 (GI Cancer Marker)", new LabTest(
                UUID.randomUUID(), "CA 199 (GI Cancer Marker)", 60.00,
                "GI/pancreatic tumor marker.",
                refLab, "CA199", false,
                "No special preparation.", "SST Gold Tube"));

        put.accept("CEA", new LabTest(
                UUID.randomUUID(), "Carcinoembryonic Antigen (CEA)", 45.00,
                "Colorectal and other adenocarcinomas.",
                refLab, "CEA", false,
                "No special preparation.", "SST Gold Tube"));

        put.accept("EBV (NPC Screen) VCA-IgA", new LabTest(
                UUID.randomUUID(), "EBV (NPC Screen) VCA-IgA", 60.00,
                "Nasopharyngeal carcinoma screening marker.",
                refLab, "EBV-IgA", false,
                "No special preparation.", "SST Gold Tube"));

        // ---------- Imaging / Services ----------
        put.accept("Ultrasound Fast Scan (Liver)", new LabTest(
                UUID.randomUUID(), "Ultrasound Fast Scan (Liver)", 80.00,
                "Quick screening ultrasound of liver.",
                refLab, "US-LIVER", false,
                "Fasting preferred.", "N/A"));

        put.accept("Ultrasound Fast Scan (Kidney)", new LabTest(
                UUID.randomUUID(), "Ultrasound Fast Scan (Kidney)", 80.00,
                "Quick screening ultrasound of kidneys.",
                refLab, "US-KIDNEY", false,
                "Well hydrated; avoid voiding 1 h prior.", "N/A"));

        put.accept("Ultrasound Fast Scan (Gallbladder)", new LabTest(
                UUID.randomUUID(), "Ultrasound Fast Scan (Gallbladder)", 80.00,
                "Quick screening ultrasound of gallbladder.",
                refLab, "US-GB", false,
                "Fast 6 hours.", "N/A"));

        put.accept("Ultrasound Fast Scan (Prostate)", new LabTest(
                UUID.randomUUID(), "Ultrasound Fast Scan (Prostate)", 90.00,
                "Quick screening ultrasound of prostate.",
                refLab, "US-PROSTATE", false,
                "Comfortably full bladder.", "N/A"));

        put.accept("Ultrasound Fast Scan (Ovary)", new LabTest(
                UUID.randomUUID(), "Ultrasound Fast Scan (Ovary)", 90.00,
                "Quick screening ultrasound of ovaries.",
                refLab, "US-OVARY", false,
                "Full bladder for transabdominal scan.", "N/A"));

        put.accept("Breast Examination", new LabTest(
                UUID.randomUUID(), "Breast Examination", 35.00,
                "Clinical breast exam by practitioner.",
                refLab, "BREAST-EXAM", false,
                "Avoid deodorant if followed by imaging.", "N/A"));

        put.accept("Pap Smear", new LabTest(
                UUID.randomUUID(), "Pap Smear", 60.00,
                "Cervical cytology screening.",
                refLab, "PAP", false,
                "Avoid intercourse/douching 24–48h; not during menses.", "N/A"));

        put.accept("ECG (12 Lead)", new LabTest(
                UUID.randomUUID(), "ECG (12 Lead)", 45.00,
                "Electrocardiogram for rhythm/ischemia.",
                refLab, "ECG12", false,
                "Rest 10 minutes before test; avoid caffeine immediately prior.", "N/A"));

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
