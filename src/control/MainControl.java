// Kong Ji Shou
package control;

import adt.ArrayList;
import adt.DictionaryInterface;
import adt.HashedDictionary;
import adt.ListInterface;
import boundary.MainUI;
import entity.*;
import entity.Doctor;
import entity.pharmacyManagement.BloodTube;
import entity.pharmacyManagement.LabTest;
import entity.pharmacyManagement.Medicine;
import entity.pharmacyManagement.SalesItem;

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
    static ListInterface<Appointment> appointments;
    static ListInterface<Staff> staff;
    static HashedDictionary<UUID, ListInterface<DutySchedule>> schedules;
    static AuthenticationControl authControl;
    static MainUI UI;

    public static void main(String[] args) throws IOException, ParseException {
        // generate data
        medicines = GeneratePharmacyData.createMedicineTable();
        labTests = GeneratePharmacyData.createLabTests();
        bloodTubes = GeneratePharmacyData.createBloodTubeInventory();
        patients = GeneratePatientData.createSamplePatients();
        doctors = GenerateDoctorData.createSampleDoctors();
        consultations = GenerateConsultationData.createSampleConsultation(doctors, patients);
        appointments = GenerateAppointmentData.createSampleAppointments(doctors, patients);
        staff = GenerateStaffData.createSampleStaff();
        authControl = new AuthenticationControl(staff);
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

        AppointmentControl appointmentControl = new AppointmentControl(patients, doctors, appointments);
        //appointmentControl.main();

        StaffControl staffControl = new StaffControl(staff, authControl);
        DoctorControl doctorControl = new DoctorControl(doctors, authControl);
        // Check if user is logged in, if not, show login screen
        if (!authControl.isLoggedIn()) {
            if (!authControl.login()) {
                System.out.println("Login failed. Exiting system.");
                return;
            }
        }
        while (true) {
            Integer choice = UI.mainMenu();
            switch (choice) {
                case 1:
                    // Search Patient
                    patient.main();
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
                case 5:
                    // Appointment
                    appointmentControl.main();
                    break;
                case 6:
                    // Staff Management
                    staffControl.main();
                    break;
                case 7:
                    // Doctor Management
                    doctorControl.main();
                    break;
                case 999:
                    authControl.logout();
                    System.out.println("Thank you for using this system!");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
