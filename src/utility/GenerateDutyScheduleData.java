package utility;

import adt.ArrayList;
import adt.HashedDictionary;
import adt.ListInterface;
import entity.DutySchedule;
import entity.Doctor;
import java.text.ParseException;
import java.util.UUID;

public class GenerateDutyScheduleData {

    // 保留原有方法（向后兼容）
    public static ListInterface<DutySchedule> createSampleDutySchedules() throws ParseException {
        ListInterface<DutySchedule> schedules = new ArrayList<>();

        schedules.add(new DutySchedule("2025-08-20", "09:00", "18:00"));
        schedules.add(new DutySchedule("2025-08-21", "09:00", "18:00"));
        schedules.add(new DutySchedule("2025-08-22", "08:00", "17:00"));
        schedules.add(new DutySchedule("2025-08-23", "13:00", "20:00"));
        schedules.add(new DutySchedule("2025-08-24", "15:00", "22:00"));

        return schedules;
    }

    // 新方法：返回HashedDictionary
    public static HashedDictionary<UUID, ListInterface<DutySchedule>> createSampleDutySchedulesDictionary(ListInterface<Doctor> doctors) throws ParseException {
        HashedDictionary<UUID, ListInterface<DutySchedule>> scheduleDict = new HashedDictionary<>();

        // 如果没有医生数据，返回空字典
        if (doctors == null || doctors.isEmpty()) {
            System.out.println("Warning: No doctors provided, returning empty schedule dictionary");
            return scheduleDict;
        }

        // 为前3个医生分配不同的排班
        int doctorCount = Math.min(doctors.size(), 3);
        for (int i = 0; i < doctorCount; i++) {
            Doctor doctor = doctors.get(i);
            ListInterface<DutySchedule> doctorSchedules = new ArrayList<>();

            // 为每个医生分配不同的示例排班
            switch (i % 3) {
                case 0:
                    doctorSchedules.add(new DutySchedule("2025-08-20", "09:00", "18:00"));
                    doctorSchedules.add(new DutySchedule("2025-08-22", "08:00", "17:00"));
                    doctorSchedules.add(new DutySchedule("2025-08-24", "15:00", "22:00"));
                    break;
                case 1:
                    doctorSchedules.add(new DutySchedule("2025-08-21", "09:00", "18:00"));
                    doctorSchedules.add(new DutySchedule("2025-08-23", "13:00", "20:00"));
                    doctorSchedules.add(new DutySchedule("2025-08-25", "10:00", "19:00"));
                    break;
                case 2:
                    doctorSchedules.add(new DutySchedule("2025-08-26", "08:30", "17:30"));
                    doctorSchedules.add(new DutySchedule("2025-08-27", "14:00", "21:00"));
                    break;
            }

            scheduleDict.add(doctor.getUserID(), doctorSchedules);
            System.out.println("Created " + doctorSchedules.size() + " schedules for doctor: " + doctor.getName());
        }

        return scheduleDict;
    }

    // 便利方法：将简单列表转换为字典（分配给第一个医生）
    public static HashedDictionary<UUID, ListInterface<DutySchedule>> convertToScheduleDictionary(
            ListInterface<DutySchedule> schedules, ListInterface<Doctor> doctors) {

        HashedDictionary<UUID, ListInterface<DutySchedule>> scheduleDict = new HashedDictionary<>();

        if (doctors != null && !doctors.isEmpty() && schedules != null && !schedules.isEmpty()) {
            // 将所有排班分配给第一个医生
            Doctor firstDoctor = doctors.get(0);
            scheduleDict.add(firstDoctor.getUserID(), schedules);
            System.out.println("Assigned " + schedules.size() + " schedules to doctor: " + firstDoctor.getName());
        }

        return scheduleDict;
    }
}