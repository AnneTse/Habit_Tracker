import java.time.LocalTime;

public class Habit {
    private String name;
    private String dayOfTheWeek;
    private LocalTime time;

    /**конструктор*/
    void setName(String name) {
        this.name = name;
    }

    void setDayOfTheWeek(String dayOfTheWeek) {
        this.dayOfTheWeek = dayOfTheWeek;
    }

    void setTime(LocalTime time) {
        this.time = time;
    }

    String getName() {
        return name;
    }

   String getDayOfTheWeek() {
        return dayOfTheWeek;
   }

    LocalTime getTime() {
        return time;
    }
}
