package control;

import adt.DictionaryInterface;
import entity.pharmacyManagement.BloodTube;
import entity.pharmacyManagement.LabTest;
import entity.pharmacyManagement.Medicine;
import utility.GeneratePharmacyData;

import java.io.IOException;
import java.text.ParseException;

import static utility.GeneratePharmacyData.createBloodTubeInventory;

public class MainControl {
    static DictionaryInterface<String, Medicine> medicines;
    static DictionaryInterface<String, LabTest> labTests;
    static DictionaryInterface<String, BloodTube> bloodTubes;



    public static void main(String[] args) throws IOException, ParseException {
        // generate data
        medicines = GeneratePharmacyData.createMedicineTable();
        labTests = GeneratePharmacyData.createLabTests();
        bloodTubes = GeneratePharmacyData.createBloodTubeInventory();
        // pharmacy module
        Pharmacy pharmacy = new Pharmacy(medicines, labTests, bloodTubes);
        pharmacy.main();
    }
}
