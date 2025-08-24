package control;

import adt.DictionaryInterface;
import adt.HashedDictionary;
import adt.ListInterface;
import entity.pharmacyManagement.BloodTube;
import entity.pharmacyManagement.LabTest;
import entity.pharmacyManagement.Medicine;
import entity.pharmacyManagement.SalesItem;
import entity.DutySchedule;
import entity.Doctor;

import utility.GeneratePharmacyData;
import utility.GenerateDoctorData;
import utility.GenerateDutyScheduleData;

import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

import static utility.GeneratePharmacyData.createBloodTubeInventory;

public class MainControl {

    static DictionaryInterface<String, Medicine> medicines;
    static DictionaryInterface<String, LabTest> labTests;
    static DictionaryInterface<String, BloodTube> bloodTubes;
    static ListInterface<Doctor> doctors;
    static HashedDictionary<UUID, ListInterface<DutySchedule>> schedules;

    public static void main(String[] args) throws IOException, ParseException {
        // generate data
        medicines = GeneratePharmacyData.createMedicineTable();
        labTests = GeneratePharmacyData.createLabTests();
        bloodTubes = GeneratePharmacyData.createBloodTubeInventory();

        doctors = GenerateDoctorData.createSampleDoctors();
        schedules = GenerateDutyScheduleData.createSampleDutySchedulesDictionary(doctors);

        // pharmacy module
        //Pharmacy pharmacy = new Pharmacy(medicines, labTests, bloodTubes);
        //pharmacy.main();

        //PatientControl patient = new PatientControl();
        //patient.main();

        //DutyScheduleControl scheduleControl = new DutyScheduleControl(doctors, schedules);
        //scheduleControl.main();

        //PrescriptionControl prescriptionControl = new PrescriptionControl(medicines);
        //prescriptionControl.main();
    }
}
