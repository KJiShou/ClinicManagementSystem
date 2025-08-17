package boundary;

import adt.ArrayList;
import adt.LinkedQueue;
import adt.ListInterface;
import adt.QueueInterface;
import entity.Doctor;
import entity.DutySchedule;
import utility.MessageUI;

import java.util.Scanner;

public class DutyScheduleUI {
    private QueueInterface<String> choiceQueue;
    private MessageUI UI;
    private static final int PAGE_SIZE = 5;
    private Scanner scanner;

    public DutyScheduleUI() {
        choiceQueue = new LinkedQueue<>();
        UI = new MessageUI();
        scanner = new Scanner(System.in);
    }

    public Integer mainMenu() {
        choiceQueue.clear();
        choiceQueue.enqueue("Add Duty Schedule");
        choiceQueue.enqueue("Update Duty Schedule");
        choiceQueue.enqueue("Delete Duty Schedule");
        choiceQueue.enqueue("View Doctor Schedule");
        return UI.mainUI("Duty Schedule Management", choiceQueue);
    }

    public Doctor selectDoctor(ListInterface<Doctor> doctors) {
        if (doctors == null || doctors.isEmpty()) {
            displayMessage("No doctors found.");
            return null;
        }

        ArrayList<Doctor> currentView = copyDoctorsList(doctors);
        int currentPage = 1;

        while (true) {
            int totalPages = calculateTotalPages(currentView.size());
            displayDoctorList(currentView, currentPage, totalPages, "");

            System.out.println("Press: [A] Prev | [D] Next | [S] Search | [Q] Quit");
            System.out.print("Enter choice or number: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("A") && currentPage > 1) {
                currentPage--;
            } else if (input.equalsIgnoreCase("D") && currentPage < totalPages) {
                currentPage++;
            } else if (input.equalsIgnoreCase("S")) {
                ArrayList<Doctor> filtered = searchDoctors(doctors);
                if (!filtered.isEmpty()) {
                    currentView = filtered;
                    currentPage = 1;
                }
            } else if (input.equalsIgnoreCase("Q")) {
                return null;
            } else {
                Doctor selected = selectDoctorByNumber(input, currentView, currentPage);
                if (selected != null) return selected;
            }
        }
    }

    public DutySchedule getDutyScheduleInput() {
        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine().trim();
        System.out.print("Enter start time (HH:MM): ");
        String start = scanner.nextLine().trim();
        System.out.print("Enter end time (HH:MM): ");
        String end = scanner.nextLine().trim();

        return new DutySchedule(date, start, end);
    }

    public void viewDoctorSchedule(Doctor doctor, ListInterface<DutySchedule> schedules) {
        if (schedules == null || schedules.isEmpty()) {
            displayMessage("No schedules for doctor " + doctor.getName());
            return;
        }

        ArrayList<DutySchedule> currentView = copySchedulesList(schedules);
        int currentPage = 1;

        while (true) {
            int totalPages = calculateTotalPages(currentView.size());
            displayScheduleList(doctor, currentView, currentPage, totalPages);

            System.out.println("Press: [A] Prev | [D] Next | [Q] Quit");
            System.out.print("Enter choice: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("A") && currentPage > 1) {
                currentPage--;
            } else if (input.equalsIgnoreCase("D") && currentPage < totalPages) {
                currentPage++;
            } else if (input.equalsIgnoreCase("Q")) {
                return;
            } else {
                displayMessage("Invalid input.");
            }
        }
    }

    public int selectScheduleFromList(Doctor doctor, ListInterface<DutySchedule> schedules) {
        if (schedules == null || schedules.isEmpty()) {
            displayMessage("No schedules for doctor " + doctor.getName());
            return -1;
        }

        ArrayList<DutySchedule> currentView = copySchedulesList(schedules);
        int currentPage = 1;

        while (true) {
            int totalPages = calculateTotalPages(currentView.size());
            displayScheduleList(doctor, currentView, currentPage, totalPages);

            System.out.println("Press: [A] Prev | [D] Next | [Number] Select | [Q] Cancel");
            System.out.print("Enter choice: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("A") && currentPage > 1) {
                currentPage--;
            } else if (input.equalsIgnoreCase("D") && currentPage < totalPages) {
                currentPage++;
            } else if (input.equalsIgnoreCase("Q")) {
                return -1;
            } else {
                int selectedIndex = selectScheduleByNumber(input, currentView, currentPage, schedules);
                if (selectedIndex != -1) return selectedIndex;
            }
        }
    }

    public void displayMessage(String msg) {
        System.out.println(msg);
    }

    private ArrayList<Doctor> copyDoctorsList(ListInterface<Doctor> doctors) {
        ArrayList<Doctor> copy = new ArrayList<>();
        for (int i = 0; i < doctors.size(); i++) {
            copy.add(doctors.get(i));
        }
        return copy;
    }

    private ArrayList<DutySchedule> copySchedulesList(ListInterface<DutySchedule> schedules) {
        ArrayList<DutySchedule> copy = new ArrayList<>();
        for (int i = 0; i < schedules.size(); i++) {
            copy.add(schedules.get(i));
        }
        return copy;
    }

    private int calculateTotalPages(int totalItems) {
        return Math.max(1, (totalItems + PAGE_SIZE - 1) / PAGE_SIZE);
    }

    private ArrayList<Doctor> searchDoctors(ListInterface<Doctor> doctors) {
        System.out.print("Search doctor by name: ");
        String query = scanner.nextLine().trim().toLowerCase();
        ArrayList<Doctor> filtered = new ArrayList<>();

        for (int i = 0; i < doctors.size(); i++) {
            Doctor d = doctors.get(i);
            if (d.getName().toLowerCase().contains(query)) {
                filtered.add(d);
            }
        }

        if (filtered.isEmpty()) {
            displayMessage("No results found for: " + query);
        }
        return filtered;
    }

    private Doctor selectDoctorByNumber(String input, ArrayList<Doctor> currentView, int currentPage) {
        try {
            int choice = Integer.parseInt(input);
            int startIndex = (currentPage - 1) * PAGE_SIZE;
            int endIndex = Math.min(currentView.size(), startIndex + PAGE_SIZE);

            if (choice >= 1 && choice <= (endIndex - startIndex)) {
                return currentView.get(startIndex + choice - 1);
            } else {
                displayMessage("Invalid number. Please select from the displayed range.");
            }
        } catch (NumberFormatException e) {
            displayMessage("Invalid input.");
        }
        return null;
    }

    private int selectScheduleByNumber(String input, ArrayList<DutySchedule> currentView, int currentPage, ListInterface<DutySchedule> originalSchedules) {
        try {
            int choice = Integer.parseInt(input);
            int startIndex = (currentPage - 1) * PAGE_SIZE;
            int endIndex = Math.min(currentView.size(), startIndex + PAGE_SIZE);

            if (choice >= 1 && choice <= (endIndex - startIndex)) {
                DutySchedule selectedSchedule = currentView.get(startIndex + choice - 1);
                for (int i = 0; i < originalSchedules.size(); i++) {
                    if (originalSchedules.get(i).equals(selectedSchedule)) {
                        return i;
                    }
                }
            } else {
                displayMessage("Invalid number. Please select from the displayed range.");
            }
        } catch (NumberFormatException e) {
            displayMessage("Invalid input.");
        }
        return -1;
    }

    private void displayDoctorList(ArrayList<Doctor> doctors, int currentPage, int totalPages, String searchQuery) {
        int start = (currentPage - 1) * PAGE_SIZE;
        int end = Math.min(doctors.size(), start + PAGE_SIZE);

        if (!searchQuery.isEmpty()) {
            System.out.println("Search query: " + searchQuery);
        }

        System.out.printf("Page %d/%d%n", currentPage, totalPages);
        System.out.println("+-----+--------------------------+");
        System.out.printf("| %-3s | %-24s |\n", "No.", "Doctor Name");
        System.out.println("+-----+--------------------------+");

        for (int i = start; i < end; i++) {
            Doctor d = doctors.get(i);
            System.out.printf("| %-3d | %-24s |\n", (i - start + 1), d.getName());
        }
        System.out.println("+-----+--------------------------+");
    }

    private void displayScheduleList(Doctor doctor, ArrayList<DutySchedule> schedules, int currentPage, int totalPages) {
        int start = (currentPage - 1) * PAGE_SIZE;
        int end = Math.min(schedules.size(), start + PAGE_SIZE);

        System.out.println("\nSchedules for " + doctor.getName());
        System.out.printf("Page %d/%d%n", currentPage, totalPages);
        System.out.println("+-----+------------+-------------------+");
        System.out.printf("| %-3s | %-10s | %-17s |\n", "No.", "Date", "Time");
        System.out.println("+-----+------------+-------------------+");

        for (int i = start; i < end; i++) {
            DutySchedule ds = schedules.get(i);
            System.out.printf("| %-3d | %-10s | %-17s |\n",
                    (i - start + 1),
                    ds.getDate(),
                    ds.getStartTime() + "-" + ds.getEndTime());
        }
        System.out.println("+-----+------------+-------------------+");
    }
}