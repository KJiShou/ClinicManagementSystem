package control;

import java.io.IOException;
import java.text.ParseException;

import static utility.GeneratePharmacyData.createBloodTubeInventory;

public class MainControl {
    public static void main(String[] args) throws IOException, ParseException {
        // pharmacy module
        //Pharmacy pharmacy = new Pharmacy();
        //pharmacy.main();

        PatientControl patient = new PatientControl();
        patient.main();
    }
}
