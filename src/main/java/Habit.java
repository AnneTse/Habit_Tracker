import java.io.Serializable;
import java.time.LocalTime;
public class Habit implements Serializable {
    private String name;
    private String dayOfTheWeek;
    private LocalTime time;
    private static final long serialVersionUID = -2529999418945499244L;
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
