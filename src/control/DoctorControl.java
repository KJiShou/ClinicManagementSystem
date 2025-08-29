package control;

import adt.ArrayList;
import adt.ListInterface;
import boundary.DoctorUI;
import entity.Doctor;
import entity.User;

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

public class DoctorControl {
    private static final int PAGE_SIZE = 5;
    private ListInterface<Doctor> doctorList;
    private AuthenticationControl authControl;
    private DoctorUI ui;
    private Scanner scanner;

    public DoctorControl(ListInterface<Doctor> doctorList, AuthenticationControl authControl) {
        this.doctorList = doctorList;
        this.authControl = authControl;
        this.ui = new DoctorUI();
        this.scanner = new Scanner(System.in);
    }

    public void main() throws IOException {
        if (!authControl.requireAnyRole(entity.Staff.Role.ADMIN, entity.Staff.Role.DOCTOR)) {
            return;
        }

        while (true) {
            Integer choice = ui.mainMenu();
            switch (choice) {
                case 1:
                    addDoctor();
                    break;
                case 2:
                    viewDoctors();
                    break;
                case 3:
                    updateDoctor();
                    break;
                case 4:
                    deleteDoctor();
                    break;
                case 5:
                    searchDoctors();
                    break;
                case 999:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public void addDoctor() {
        if (!authControl.requireRole(entity.Staff.Role.ADMIN)) {
            return;
        }
        
        System.out.println("\n=== ADD DOCTOR ===");
        Doctor newDoctor = ui.getDoctorDetails();
        
        if (newDoctor == null) {
            ui.displayMessage("Doctor registration cancelled.");
            ui.pause();
            return;
        }

        if (validateDoctorData(newDoctor)) {
            doctorList.add(newDoctor);
            ui.displaySuccess("Doctor added successfully!");
            System.out.println("Doctor Details:");
            System.out.println("- Name: " + newDoctor.getName());
            System.out.println("- Specialization: " + newDoctor.getSpecialization());
            System.out.println("- License: " + newDoctor.getLicenseNumber());
        }
        ui.pause();
    }

    public void viewDoctors() {
        System.out.println("\n=== VIEW DOCTORS ===");
        
        if (doctorList.isEmpty()) {
            ui.displayMessage("No doctors found.");
            ui.pause();
            return;
        }

        ArrayList<Doctor> doctorArray = convertToArrayList();
        displayDoctorsPaginated(doctorArray, "All Doctors");
    }

    public void updateDoctor() {
        if (!authControl.requireRole(entity.Staff.Role.ADMIN)) {
            return;
        }
        
        System.out.println("\n=== UPDATE DOCTOR ===");
        
        if (doctorList.isEmpty()) {
            ui.displayMessage("No doctors found.");
            ui.pause();
            return;
        }

        ArrayList<Doctor> doctorArray = convertToArrayList();
        Doctor selectedDoctor = selectDoctor(doctorArray);
        
        if (selectedDoctor != null) {
            updateDoctorDetails(selectedDoctor);
        }
    }

    public void deleteDoctor() {
        if (!authControl.requireRole(entity.Staff.Role.ADMIN)) {
            return;
        }
        
        System.out.println("\n=== DELETE DOCTOR ===");
        
        if (doctorList.isEmpty()) {
            ui.displayMessage("No doctors found.");
            ui.pause();
            return;
        }

        ArrayList<Doctor> doctorArray = convertToArrayList();
        Doctor selectedDoctor = selectDoctor(doctorArray);
        
        if (selectedDoctor != null) {
            System.out.println("\nDoctor to Delete:");
            displayDoctorDetails(selectedDoctor);
            System.out.print("\nConfirm deletion? (Y/N): ");
            String confirm = scanner.nextLine().trim().toUpperCase();

            if (confirm.equals("Y")) {
                for (int i = 0; i < doctorList.size(); i++) {
                    if (doctorList.get(i).getUserID().equals(selectedDoctor.getUserID())) {
                        doctorList.remove(i);
                        break;
                    }
                }
                ui.displaySuccess("Doctor deleted successfully!");
            } else {
                ui.displayMessage("Deletion cancelled.");
            }
        }
        ui.pause();
    }

    public void searchDoctors() {
        System.out.println("\n=== SEARCH DOCTORS ===");
        System.out.print("Enter search term (Name/Specialization/License): ");
        String searchTerm = scanner.nextLine().trim();

        if (searchTerm.isEmpty()) {
            ui.displayMessage("Search cancelled.");
            ui.pause();
            return;
        }

        ArrayList<Doctor> results = searchDoctorList(searchTerm);
        
        if (results.isEmpty()) {
            ui.displayMessage("No doctors found matching: " + searchTerm);
        } else {
            displayDoctorsPaginated(results, "Search Results: " + searchTerm);
        }
    }

    private boolean validateDoctorData(Doctor doctor) {
        if (doctor.getName() == null || doctor.getName().trim().isEmpty()) {
            ui.displayError("Name is required.");
            return false;
        }
        
        if (doctor.getSpecialization() == null || doctor.getSpecialization().trim().isEmpty()) {
            ui.displayError("Specialization is required.");
            return false;
        }
        
        if (doctor.getLicenseNumber() == null || doctor.getLicenseNumber().trim().isEmpty()) {
            ui.displayError("License number is required.");
            return false;
        }
        
        return true;
    }

    private ArrayList<Doctor> convertToArrayList() {
        ArrayList<Doctor> result = new ArrayList<>();
        for (int i = 0; i < doctorList.size(); i++) {
            result.add(doctorList.get(i));
        }
        return result;
    }

    private Doctor selectDoctor(ArrayList<Doctor> doctors) {
        while (true) {
            ui.displayDoctorSelectionList(doctors);
            System.out.printf("Select doctor (1-%d) or 0 to cancel: ", doctors.size());
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice == 0) return null;
                if (choice >= 1 && choice <= doctors.size()) {
                    return doctors.get(choice - 1);
                }
                System.out.println("ERROR: Invalid selection.");
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Please enter a valid number.");
            }
        }
    }

    private void displayDoctorDetails(Doctor doctor) {
        System.out.println("-".repeat(50));
        System.out.println("DOCTOR DETAILS");
        System.out.println("-".repeat(50));
        System.out.println("ID: " + doctor.getUserID());
        System.out.println("Name: " + doctor.getName());
        System.out.println("Specialization: " + doctor.getSpecialization());
        System.out.println("License Number: " + doctor.getLicenseNumber());
        System.out.println("Phone: " + doctor.getPhone());
        System.out.println("Email: " + doctor.getEmail());
        System.out.println("Address: " + doctor.getAddress());
        System.out.println("-".repeat(50));
    }

    private void updateDoctorDetails(Doctor doctor) {
        while (true) {
            System.out.println("\n=== UPDATE DOCTOR ===");
            displayDoctorDetails(doctor);
            
            System.out.println("\nWhat would you like to update?");
            System.out.println("1. Name");
            System.out.println("2. Specialization");
            System.out.println("3. License Number");
            System.out.println("4. Phone");
            System.out.println("5. Email");
            System.out.println("6. Address");
            System.out.println("9. Save and Exit");
            System.out.print("Choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        System.out.print("New name: ");
                        String newName = scanner.nextLine().trim();
                        if (!newName.isEmpty()) {
                            doctor.setName(newName);
                            System.out.println("SUCCESS: Name updated.");
                        }
                        break;
                    case 2:
                        System.out.print("New specialization: ");
                        String newSpec = scanner.nextLine().trim();
                        if (!newSpec.isEmpty()) {
                            doctor.setSpecialization(newSpec);
                            System.out.println("SUCCESS: Specialization updated.");
                        }
                        break;
                    case 3:
                        System.out.print("New license number: ");
                        String newLicense = scanner.nextLine().trim();
                        if (!newLicense.isEmpty()) {
                            doctor.setLicenseNumber(newLicense);
                            System.out.println("SUCCESS: License number updated.");
                        }
                        break;
                    case 4:
                        System.out.print("New phone: ");
                        String newPhone = scanner.nextLine().trim();
                        if (!newPhone.isEmpty()) {
                            doctor.setPhone(newPhone);
                            System.out.println("SUCCESS: Phone updated.");
                        }
                        break;
                    case 5:
                        System.out.print("New email: ");
                        String newEmail = scanner.nextLine().trim();
                        if (!newEmail.isEmpty()) {
                            doctor.setEmail(newEmail);
                            System.out.println("SUCCESS: Email updated.");
                        }
                        break;
                    case 6:
                        System.out.print("New address: ");
                        String newAddress = scanner.nextLine().trim();
                        if (!newAddress.isEmpty()) {
                            doctor.setAddress(newAddress);
                            System.out.println("SUCCESS: Address updated.");
                        }
                        break;
                    case 9:
                        ui.displaySuccess("Doctor details updated successfully!");
                        ui.pause();
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private ArrayList<Doctor> searchDoctorList(String searchTerm) {
        ArrayList<Doctor> results = new ArrayList<>();
        String term = searchTerm.toLowerCase();
        
        for (int i = 0; i < doctorList.size(); i++) {
            Doctor doctor = doctorList.get(i);
            if (doctor.getName().toLowerCase().contains(term) ||
                doctor.getSpecialization().toLowerCase().contains(term) ||
                doctor.getLicenseNumber().toLowerCase().contains(term)) {
                results.add(doctor);
            }
        }
        
        return results;
    }

    private void displayDoctorsPaginated(ArrayList<Doctor> doctors, String title) {
        int currentPage = 1;
        
        while (true) {
            int totalPages = (doctors.size() + PAGE_SIZE - 1) / PAGE_SIZE;
            if (totalPages == 0) totalPages = 1;
            
            ui.displayDoctorList(doctors, currentPage, totalPages, "", title);
            
            System.out.println("Press: [A] Prev | [D] Next | [V] View Details | [Q] Back");
            System.out.print("Enter choice: ");
            String input = scanner.nextLine().trim().toLowerCase();
            
            switch (input) {
                case "a":
                    if (currentPage > 1) currentPage--;
                    else System.out.println("This is the first page.");
                    break;
                case "d":
                    if (currentPage < totalPages) currentPage++;
                    else System.out.println("This is the last page.");
                    break;
                case "v":
                    Doctor selected = selectDoctor(doctors);
                    if (selected != null) {
                        displayDoctorDetails(selected);
                        ui.pause();
                    }
                    break;
                case "q":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}