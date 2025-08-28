package control;

import adt.ArrayList;
import adt.DictionaryInterface;
import adt.HashedDictionary;
import adt.ListInterface;
import boundary.MainUI;
import entity.Consultation;
import entity.Patient;
import entity.pharmacyManagement.BloodTube;
import entity.pharmacyManagement.LabTest;
import entity.pharmacyManagement.Medicine;
import entity.pharmacyManagement.SalesItem;
import entity.DutySchedule;
import entity.Doctor;

import utility.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

public class MainControl {

    static DictionaryInterface<String, Medicine> medicines;
    static DictionaryInterface<String, LabTest> labTests;
    static DictionaryInterface<String, BloodTube> bloodTubes;
    static ArrayList<Patient> patients;
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
        patients = GeneratePatientData.createSamplePatients();
        doctors = GenerateDoctorData.createSampleDoctors();
        schedules = GenerateDutyScheduleData.createSampleDutySchedulesDictionary(doctors);
        UI = new MainUI();

        // pharmacy module
        PharmacyControl pharmacy = new PharmacyControl(medicines, labTests, bloodTubes);
        //pharmacy.main();

        PatientControl patient = new PatientControl(patients, consultations, doctors);
        //patient.main();

        DutyScheduleControl scheduleControl = new DutyScheduleControl(doctors, schedules, consultations);
        //scheduleControl.main();

        PrescriptionControl prescriptionControl = new PrescriptionControl(medicines, pharmacy);
        //prescriptionControl.main();

        ConsultationControl consultationControl = new ConsultationControl(consultations, patients, doctors, prescriptionControl, scheduleControl, pharmacy);
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
                    consultationControl.main();
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
