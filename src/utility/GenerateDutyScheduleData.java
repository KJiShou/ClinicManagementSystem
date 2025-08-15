package utility;

import adt.ArrayList;
import adt.ListInterface;
import entity.DutySchedule;
import java.time.LocalTime;

public class GenerateDutyScheduleData {

    public static ListInterface<DutySchedule> createSampleDutySchedules() {
        ListInterface<DutySchedule> schedules = new ArrayList<>();

        schedules.add(new DutySchedule(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                20, 8, 2025
        ));

        schedules.add(new DutySchedule(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                21, 8, 2025
        ));

        schedules.add(new DutySchedule(
                LocalTime.of(8, 0),
                LocalTime.of(17, 0),
                22, 8, 2025
        ));

        schedules.add(new DutySchedule(
                LocalTime.of(13, 0),
                LocalTime.of(20, 0),
                23, 8, 2025
        ));

        schedules.add(new DutySchedule(
                LocalTime.of(15, 0),
                LocalTime.of(22, 0),
                24, 8, 2025
        ));

        return schedules;
    }
}
