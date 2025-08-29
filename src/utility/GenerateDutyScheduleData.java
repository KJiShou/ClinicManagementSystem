package utility;

import adt.ArrayList;
import adt.HashedDictionary;
import adt.ListInterface;
import entity.DutySchedule;
import entity.Doctor;
import java.text.ParseException;
import java.util.UUID;

public class GenerateDutyScheduleData {

    public static HashedDictionary<UUID, ListInterface<DutySchedule>> createSampleDutySchedulesDictionary(ListInterface<Doctor> doctors) throws ParseException {
        HashedDictionary<UUID, ListInterface<DutySchedule>> scheduleDict = new HashedDictionary<>();

        //return null when no doctor
        if (doctors == null || doctors.isEmpty()) {
            System.out.println("Warning: No doctors provided, returning empty schedule dictionary");
            return scheduleDict;
        }

        //set limit 4 for doctor(hardcore sample data), assign different working time
        int doctorCount = Math.min(doctors.size(), 4);

        for (int i = 0; i < doctorCount; i++) {
            Doctor doctor = doctors.get(i);
            ListInterface<DutySchedule> doctorSchedules = new ArrayList<>();

            switch (i % 4) {
                case 0: //doctor1 - js
                    doctorSchedules.add(new DutySchedule("2025-08-20", "09:00", "18:00"));
                    doctorSchedules.add(new DutySchedule("2025-08-22", "08:00", "17:00"));
                    doctorSchedules.add(new DutySchedule("2025-08-24", "15:00", "22:00"));
                    doctorSchedules.add(new DutySchedule("2025-08-28", "08:00", "18:00"));
                    doctorSchedules.add(new DutySchedule("2025-08-29", "14:00", "21:00"));
                    break;
                case 1: //doctor2 - jy
                    doctorSchedules.add(new DutySchedule("2025-08-21", "09:00", "18:00"));
                    doctorSchedules.add(new DutySchedule("2025-08-23", "13:00", "20:00"));
                    doctorSchedules.add(new DutySchedule("2025-08-27", "14:00", "19:00"));
                    doctorSchedules.add(new DutySchedule("2025-08-28", "08:00", "19:00"));
                    doctorSchedules.add(new DutySchedule("2025-08-29", "13:00", "20:00"));
                    break;
                case 2: //doctor3 - alex
                    doctorSchedules.add(new DutySchedule("2025-08-25", "08:00", "16:00"));
                    doctorSchedules.add(new DutySchedule("2025-08-28", "10:00", "18:00"));
                    break;
                case 3: //doctor4 - susan
                    doctorSchedules.add(new DutySchedule("2025-08-27", "08:00", "14:00"));
                    doctorSchedules.add(new DutySchedule("2025-08-28", "09:00", "19:00"));
                    doctorSchedules.add(new DutySchedule("2025-08-29", "08:00", "18:00"));
                    doctorSchedules.add(new DutySchedule("2025-09-02", "12:30", "21:30"));
                    break;
            }

            scheduleDict.add(doctor.getUserID(), doctorSchedules);
            System.out.println("Created " + doctorSchedules.size() + " schedules for doctor: " + doctor.getName());
        }
        return scheduleDict;
    }
}