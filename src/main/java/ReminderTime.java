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
                        habit.setDayOfTheWeek(userHabits.get(j)[1].split(" "));
                        habit.setTime(LocalTime.parse(userHabits.get(j)[2]));

                        String day = weekDay();

                        if (Arrays.asList(habit.getDayOfTheWeek()).contains(day) &&
                        habit.getTime().getHour() == time.getHour() &&
                        habit.getTime().getMinute() == time.getMinute()) {
                            silent.send(("Напоминаем о том, что вы планируте " + userHabits.get(j)[0]), users.get(i));
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
            case 1: return "Воскресенье";
            case 2: return "Понедельник";
            case 3: return "Вторник";
            case 4: return "Среда";
            case 5: return "Четверг";
            case 6: return "Пятница";
            case 7: return "Суббота";
            default: return "";
        }
    }
}
