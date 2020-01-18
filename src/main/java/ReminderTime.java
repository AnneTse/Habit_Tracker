import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

/**Класс отвечающий напоминания*/
public class ReminderTime {
    public ReminderTime(DBContext db, SilentSender silent) {

        Habit habit = new Habit();

        TimerTask newTask = new TimerTask() {
            @Override

            public void run() {
                LocalTime time = LocalTime.now(ZoneId.systemDefault());
                Map<Integer, Integer> users = db.getMap("usersId");
                for (int i : users.keySet()) {
                    Map<Integer, String[]> userHabits = db.getMap(String.valueOf(users.get(i)));
                    for (int j :  userHabits.keySet()) {
                        habit.setName(userHabits.get(j)[0]);
                        habit.setDayOfTheWeek(userHabits.get(j)[1]);
                        habit.setTime(LocalTime.parse(userHabits.get(j)[2]));

                        String day = weekDay();

                        if (Arrays.asList(habit.getDayOfTheWeek()).contains(day) &&
                        habit.getTime().getHour() == time.getHour() &&
                        habit.getTime().getMinute() == time.getMinute()) {
                            silent.send(("We remind you that you plan to " + userHabits.get(j)[0]), users.get(i));
                        }
                    }
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(newTask, 1, 60*1000);
    }

    private String weekDay() {
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)){
            case 1: return "Sunday";
            case 2: return "Monday";
            case 3: return "Tuesday";
            case 4: return "Wednesday";
            case 5: return "Thursday";
            case 6: return "Friday";
            case 7: return "Saturday";
            default: return "";
        }
    }
}
