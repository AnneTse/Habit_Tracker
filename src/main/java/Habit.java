import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Map;

public class Habit {

    private ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

    private Map <Integer,String> HabitMap;

    Habit(DBContext db) {
        HabitMap = db.getMap("Habit");
    }

    public void  addHabit(String habitName) {
        int q = 0;
        for (int i : HabitMap.keySet()) {
            q++;
        }
        //HabitMap.put(q, habitName);
        HabitMap.put(HabitMap.size(), habitName);
    }

    String[] get() {
        String[] myArray = new String[HabitMap.size()];

        for (int i : HabitMap.keySet()) {
            myArray[i] = (HabitMap.get(i));
        }
        return myArray;
    }

    public void remove() {
        for (int i : HabitMap.keySet()) {
            HabitMap.remove(i);
        }
    }
}
