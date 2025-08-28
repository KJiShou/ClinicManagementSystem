// Chea Hong Jun
package boundary;

import adt.*;
import entity.Doctor;
import entity.DutySchedule;
import utility.MessageUI;

import java.util.Scanner;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

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
        choiceQueue.enqueue("View Clinic Schedule");
        choiceQueue.enqueue("View Doctor Schedule");
        choiceQueue.enqueue("Generate Report");
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
                displayMessage("");
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

    private LocalTime clampToRange(LocalTime t, LocalTime lo, LocalTime hi) {
        if (t.isBefore(lo)) return lo;
        if (!t.isBefore(hi)) return hi;
        return t;
    }
    private String getInitials(String name) {
        if (name == null || name.isBlank()) return "?";
        String normalized = name.trim().replaceAll("[^A-Za-z]+", " ");
        if (normalized.isEmpty()) return "?";

        String[] parts = normalized.split("\\s+");
        String[] skip = {"DR", "DOCTOR"};
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String token = parts[i].toUpperCase();
            boolean isSkip = false;
            for (int k = 0; k < skip.length; k++) {
                if (token.equals(skip[k])) { isSkip = true; break; }
            }
            if (!isSkip) sb.append(token.charAt(0));
        }
        return sb.length() == 0 ? "?" : sb.toString();
    }
    private void printHLine(int timeCol, int colWidth) {
        System.out.print("+"); System.out.print(repeat("-", timeCol)); System.out.print("+");
        for (int i = 0; i < 7; i++) { System.out.print(repeat("-", colWidth)); System.out.print("+"); }
        System.out.println();
    }
    private String padRight(String s, int width) {
        if (s == null) s = "";
        if (s.length() >= width) return s.substring(0, width);
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < width) sb.append(' ');
        return sb.toString();
    }
    private String repeat(String s, int n) { StringBuilder sb = new StringBuilder(); for (int i=0;i<n;i++) sb.append(s); return sb.toString(); }
    private String centerLine(String s, int width) {
        if (s.length() >= width) return s.substring(0, width);
        int left = (width - s.length()) / 2; int right = width - s.length() - left;
        return repeat(" ", left) + s + repeat(" ", right);
    }

    private String padCenter(String s, int width) {
        if (s == null) s = "";
        if (s.length() >= width) return s.substring(0, width);
        int left = (width - s.length()) / 2;
        int right = width - s.length() - left;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < left; i++) sb.append(' ');
        sb.append(s);
        for (int i = 0; i < right; i++) sb.append(' ');
        return sb.toString();
    }

    public void viewDoctorWeekGridStars(
            Doctor doctor,
            ListInterface<DutySchedule> schedules,
            LocalDate weekStart,
            LocalTime dayStart,
            LocalTime dayEnd,
            int slotMinutes
    ) {
        if (doctor == null) { displayMessage("No doctor selected."); return; }
        if (schedules == null || schedules.isEmpty()) {
            displayMessage("No schedules for " + doctor.getName());
            return;
        }

        String[] dayHeaders = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
        DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

        int totalSlots = (int)(Duration.between(dayStart, dayEnd).toMinutes() / slotMinutes);
        if (totalSlots <= 0) { displayMessage("Invalid time range."); return; }

        String[][] cell = new String[totalSlots][7];
        for (int r = 0; r < totalSlots; r++) {
            for (int c = 0; c < 7; c++) cell[r][c] = "";
        }

        String init = getInitials(doctor.getName());
        LocalDate weekEnd = weekStart.plusDays(7);

        for (int j = 0; j < schedules.size(); j++) {
            DutySchedule ds = schedules.get(j);
            LocalDate d; LocalTime s; LocalTime e;
            try {
                d = LocalDate.parse(ds.getDate(), DATE_FMT);
                s = LocalTime.parse(ds.getStartTime(), TIME_FMT);
                e = LocalTime.parse(ds.getEndTime(), TIME_FMT);
            } catch (Exception ex) { continue; }
            if (!e.isAfter(s)) continue;
            if (d.isBefore(weekStart) || !d.isBefore(weekEnd)) continue;

            int col = d.getDayOfWeek().getValue() - 1; // Mon=1 -> 0
            LocalTime s2 = clampToRange(s, dayStart, dayEnd);
            LocalTime e2 = clampToRange(e, dayStart, dayEnd);
            int startIdx = (int)(Duration.between(dayStart, s2).toMinutes() / slotMinutes);
            int endIdx   = (int)(Duration.between(dayStart, e2).toMinutes() / slotMinutes);

            if (endIdx <= 0 || startIdx >= totalSlots) continue;
            startIdx = Math.max(0, startIdx);
            endIdx   = Math.min(totalSlots, endIdx);

            if (endIdx - startIdx == 1) {
                cell[startIdx][col] = init;
            } else {
                cell[startIdx][col] = init;
                for (int r = startIdx + 1; r < endIdx - 1; r++) {
                    cell[r][col] = "*";
                }
                cell[endIdx - 1][col] = init;
            }
        }

        // ===== 渲染 =====
        int COL_WIDTH = 10;
        int TIME_COL  = 8;
        String title = "Doctor Weekly Schedule - " + doctor.getName()
                + "   " + weekStart + " - " + weekStart.plusDays(6);

        System.out.println();
        System.out.println(centerLine(title, TIME_COL + 7 * (COL_WIDTH + 1) + 1));
        printHLine(TIME_COL, COL_WIDTH);

        System.out.print("| " + padRight("Time", TIME_COL - 2) + " |");
        for (String h : dayHeaders) System.out.print(" " + padRight(h, COL_WIDTH - 2) + " |");
        System.out.println();
        printHLine(TIME_COL, COL_WIDTH);

        for (int r = 0; r < totalSlots; r++) {
            LocalTime slotTime = dayStart.plusMinutes((long) r * slotMinutes);
            System.out.print("| " + padRight(slotTime.toString(), TIME_COL - 2) + " |");
            for (int c = 0; c < 7; c++) {
                System.out.print(" " + padCenter(cell[r][c], COL_WIDTH - 2) + " |");
            }
            System.out.println();
            printHLine(TIME_COL, COL_WIDTH);
        }

        System.out.println("\nDoctor: " + init + " = " + doctor.getName());
    }

    public void viewDoctorWeekGridStarsInteractive(
            Doctor doctor,
            ListInterface<DutySchedule> schedules
    ) {
        LocalDate monday = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        LocalTime dayStart = LocalTime.of(8, 0);
        LocalTime dayEnd   = LocalTime.of(20, 0);
        int slotMinutes    = 60;

        while (true) {
            viewDoctorWeekGridStars(doctor, schedules, monday, dayStart, dayEnd, slotMinutes);
            System.out.println("\n[A] Prev week  |  [D] Next week  |  [Q] Back");
            System.out.print("Enter choice: ");
            String in = scanner.nextLine().trim();
            if (in.equalsIgnoreCase("A")) monday = monday.minusWeeks(1);
            else if (in.equalsIgnoreCase("D")) monday = monday.plusWeeks(1);
            else if (in.equalsIgnoreCase("Q")) return;
        }
    }

    public void viewClinicWeekGridStarsInteractive(
            ListInterface<Doctor> doctors,
            HashedDictionary<UUID, ListInterface<DutySchedule>> schedules
    ) {
        LocalDate monday = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        LocalTime dayStart = LocalTime.of(8, 0);
        LocalTime dayEnd   = LocalTime.of(20, 0);
        int slotMinutes    = 60;

        while (true) {
            renderClinicWeekGridStars(doctors, schedules, monday, dayStart, dayEnd, slotMinutes);
            System.out.println("\n[A] Prev week  |  [D] Next week  |  [Q] Back");
            System.out.print("Enter choice: ");
            String in = scanner.nextLine().trim();
            if (in.equalsIgnoreCase("A")) monday = monday.minusWeeks(1);
            else if (in.equalsIgnoreCase("D")) monday = monday.plusWeeks(1);
            else if (in.equalsIgnoreCase("Q")) return;
        }
    }

    public void viewClinicWeekGridStars(
            ListInterface<Doctor> doctors,
            HashedDictionary<UUID, ListInterface<DutySchedule>> schedules,
            LocalDate weekStart,
            LocalTime dayStart,
            LocalTime dayEnd,
            int slotMinutes
    ) {
        renderClinicWeekGridStars(doctors, schedules, weekStart, dayStart, dayEnd, slotMinutes);
        System.out.println("Enter to continue...");
        scanner.nextLine();
    }

    private void renderClinicWeekGridStars(
            ListInterface<Doctor> doctors,
            HashedDictionary<UUID, ListInterface<DutySchedule>> schedules,
            LocalDate weekStart,
            LocalTime dayStart,
            LocalTime dayEnd,
            int slotMinutes
    ) {
        if (doctors == null || doctors.isEmpty()) { displayMessage("No doctors."); return; }
        if (schedules == null || schedules.isEmpty()) { displayMessage("No schedules."); return; }

        String[] dayHeaders = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
        DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
        int totalSlots = (int)(Duration.between(dayStart, dayEnd).toMinutes() / slotMinutes);
        if (totalSlots <= 0) { displayMessage("Invalid time range."); return; }

        String[][] cell = new String[totalSlots][7];
        for (int r=0;r<totalSlots;r++) for (int c=0;c<7;c++) cell[r][c]="";

        HashedDictionary<UUID,String> initialsMap = new HashedDictionary<>();
        for (int i=0;i<doctors.size();i++) {
            Doctor d = doctors.get(i);
            initialsMap.add(d.getUserID(), getInitials(d.getName()));
        }

        LocalDate weekEnd = weekStart.plusDays(7);

        for (int i=0;i<doctors.size();i++) {
            Doctor doc = doctors.get(i);
            ListInterface<DutySchedule> list = schedules.getValue(doc.getUserID());
            if (list==null) continue;

            for (int j=0;j<list.size();j++) {
                DutySchedule ds = list.get(j);
                LocalDate d; LocalTime s; LocalTime e;
                try {
                    d = LocalDate.parse(ds.getDate(), DATE_FMT);
                    s = LocalTime.parse(ds.getStartTime(), TIME_FMT);
                    e = LocalTime.parse(ds.getEndTime(), TIME_FMT);
                } catch (Exception ex) { continue; }
                if (!e.isAfter(s)) continue;
                if (d.isBefore(weekStart) || !d.isBefore(weekEnd)) continue;

                int col = d.getDayOfWeek().getValue()-1;
                LocalTime s2 = clampToRange(s, dayStart, dayEnd);
                LocalTime e2 = clampToRange(e, dayStart, dayEnd);
                int startIdx=(int)(Duration.between(dayStart,s2).toMinutes()/slotMinutes);
                int endIdx  =(int)(Duration.between(dayStart,e2).toMinutes()/slotMinutes);
                if (endIdx<=0 || startIdx>=totalSlots) continue;
                startIdx=Math.max(0,startIdx);
                endIdx=Math.min(totalSlots,endIdx);

                String init = initialsMap.getValue(doc.getUserID());
                if (init==null) init="??";

                if (endIdx-startIdx==1) {
                    cell[startIdx][col] = init;
                } else {
                    cell[startIdx][col] = init;
                    for (int r=startIdx+1; r<endIdx-1; r++) {
                        cell[r][col] = "*";
                    }
                    cell[endIdx-1][col] = init;
                }
            }
        }

        int COL_WIDTH=10, TIME_COL=8;
        String title = "Clinic Schedule " + weekStart + " - " + weekStart.plusDays(6);
        System.out.println("\n"+centerLine(title,TIME_COL+7*(COL_WIDTH+1)+1));
        printHLine(TIME_COL,COL_WIDTH);

        System.out.print("| "+padRight("Time",TIME_COL-2)+" |");
        for (String h: dayHeaders) System.out.print(" "+padRight(h,COL_WIDTH-2)+" |");
        System.out.println();
        printHLine(TIME_COL,COL_WIDTH);

        for (int r=0;r<totalSlots;r++) {
            LocalTime slotTime=dayStart.plusMinutes((long)r*slotMinutes);
            System.out.print("| " + padRight(slotTime.toString(), TIME_COL - 2) + " |");
            for (int c = 0; c < 7; c++) {
                String content = cell[r][c];
                System.out.print(" " + padCenter(content, COL_WIDTH - 2) + " |");
            }
            System.out.println();
            printHLine(TIME_COL,COL_WIDTH);
        }

        System.out.println("\nDoctor:");
        for (int i=0;i<doctors.size();i++) {
            Doctor d=doctors.get(i);
            System.out.println(getInitials(d.getName())+" = "+d.getName());
        }
    }
}