package control;

import adt.DictionaryInterface;
import adt.HashedDictionary;
import adt.ListInterface;
import boundary.MainUI;
import entity.Consultation;
import entity.pharmacyManagement.BloodTube;
import entity.pharmacyManagement.LabTest;
import entity.pharmacyManagement.Medicine;
import entity.DutySchedule;
import entity.Doctor;

import utility.GenerateConsultationData;
import utility.GeneratePharmacyData;
import utility.GenerateDoctorData;
import utility.GenerateDutyScheduleData;

import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

public class MainControl {

    static DictionaryInterface<String, Medicine> medicines;
    static DictionaryInterface<String, LabTest> labTests;
    static DictionaryInterface<String, BloodTube> bloodTubes;
    static ListInterface<Doctor> doctors;
    static ListInterface<Consultation> consultations;
    static HashedDictionary<UUID, ListInterface<DutySchedule>> schedules;
    static MainUI UI;

    public static void main(String[] args) throws IOException, ParseException {
        // generate data
        medicines = GeneratePharmacyData.createMedicineTable();
        labTests = GeneratePharmacyData.createLabTests();
        bloodTubes = GeneratePharmacyData.createBloodTubeInventory();
        consultations = GenerateConsultationData.createSampleConsultation();
        doctors = GenerateDoctorData.createSampleDoctors();
        schedules = GenerateDutyScheduleData.createSampleDutySchedulesDictionary(doctors);
        UI = new MainUI();

        // pharmacy module
        PharmacyControl pharmacy = new PharmacyControl(medicines, labTests, bloodTubes);
        //pharmacy.main();

        PatientControl patient = new PatientControl();
        //patient.main();

        DutyScheduleControl scheduleControl = new DutyScheduleControl(doctors, schedules);
        //scheduleControl.main();

        ConsultationControl consultationControl = new ConsultationControl(consultations);
        //consultationControl.main();
        while (true) {
            Integer choice = UI.mainMenu();
            switch (choice) {
                case 1:
                    // Search Patient
                    patient.displayPatient();
                    break;
                case 2:
                    // Consultation
                    consultationControl.viewConsultation();
                    break;
                case 3:
                    // Pharmacy
                    pharmacy.main();
                    break;
                case 4:
                    // Duty Schedule
                    scheduleControl.main();
                    break;
                case 999:
                    System.out.println("Thank you for using this system!");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
