import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.*;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AnneAbilityExtension  implements AbilityExtension  {

    private ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
    private SilentSender silent;
    private DBContext db;
    private String[] habits = {"Бросить курить", "Заняться спортом"};

    AnneAbilityExtension(SilentSender silent, DBContext db) {
        this.silent = silent;
        this.db = db;
        ReminderTime reminderTime = new ReminderTime(db,silent);
    }

    /**Start*/
    public Reply start() {
        return Reply.of(update -> {
            Map<Integer, Integer> users = db.getMap("usersId");
            if (!users.containsValue(update.getMessage().getFrom().getId())) {
                users.put(users.size(), update.getMessage().getFrom().getId());
            }
            SendMessage sendMessage = new SendMessage().setChatId(update.getMessage().getChatId());
            sendMessage.setText("Доброго времени суток, этот бот поможет Вам освоить полезные привычки." +
                    "Вы можете выбрать нужную из меню снизу или создать свою. \n\n" +
                    "Чтобы вывести список ваших привычек напишите /myHabits \n" +
                    "Для удалвения всех превычек введите /delete \n" +
                    "Чтобы трекнуть освоенную привычку напишите /mark \n");
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
            addKeyboard(replyKeyboardMarkup, new String[]{"Бросить курить", "Заняться спортом", "Add new habit"});
            silent.execute(sendMessage);
        }, update -> update.getMessage().getText().equals("/start"));
    }

    /**мои привычки*/
    public Reply myHabit() {
        return Reply.of(update -> {
            long userId = update.getMessage().getFrom().getId();
            Map <Integer,String[]> userHabits = db.getMap(String.valueOf(userId));
            /**вывод списка привычек на экран*/
            String q = "";
            for (int i: userHabits.keySet()){
                q += "Habit:  " + userHabits.get(i)[0] +
                        "\n Days:  " + userHabits.get(i)[1] +
                        "\n Time:  " + userHabits.get(i)[2] + "\n\n";
            }
            silent.send((q), update.getMessage().getChatId());
        }, update -> update.getMessage().getText().equals("/myHabits"));
    }

    /**добавление привычки*/
    public Reply addHabit() {
        return Reply.of(update -> {
            Integer userId = update.getMessage().getFrom().getId();
            String message = update.getMessage().getText();

            Map<Integer, String[]> newHabit = db.getMap("newHabit");

            if (Arrays.asList(habits).contains(message)) {
                newHabit.put(userId,new String[]{message, null, null});
                silent.send("Выберете день недели или перечислите нужные через пробел (Например: Понедельник Среда)", update.getMessage().getChatId());
            } else if (message.equals("Add new habit")) {
                newHabit.put(userId, new String[]{null, null, null});
                silent.send("Введите название привычки",update.getMessage().getChatId());
            } else if (newHabit.containsKey(userId)) {
                if (newHabit.get(userId)[0] == null) {
                    newHabit.put(userId, new String[] {message, null, null});
                    silent.send("Введите день недели или перечислите нужные через пробел (Например: Понедельник Среда)", update.getMessage().getChatId());
                }  else if (newHabit.get(userId)[1] == null) {
                    newHabit.put(userId, new String[] {newHabit.get(userId)[0], message, null});
                    silent.send("Введите время в формате чч:мм (Например: 03:09)", update.getMessage().getChatId());
                }  else if (newHabit.get(userId)[2] == null) {
                    newHabit.put(userId, new String[] { newHabit.get(userId)[0], newHabit.get(userId)[1], message});

                    Map<Integer, String[]> userHabits;
                    userHabits = db.getMap(String.valueOf(userId));
                    userHabits.put(userHabits.size(), newHabit.get(userId));
                    newHabit.remove(userId);

                    silent.send("Вы создали привычку и теперь постарайтесь следовать ей", update.getMessage().getChatId());
                }
            }
        }, Flag.TEXT);
    }

    /**Удаление всех привычек пользователей*/
//    public Reply deleteAllHabits() {
//        return Reply.of(update -> {
//            Map<Integer, Integer> users = db.getMap("usersId");
//            Map<Integer,String[]> newHabit = db.getMap("Add new habit");
//
//            for (int i : users.keySet()) {
//                newHabit.remove(i);
//
//                Map<Integer, String[]> userHabits = db.getMap(String.valueOf(users.get(i)));
//
//                for (int j : userHabits.keySet()) {
//                    userHabits.remove(i);
//                }
//            }
//            silent.send("Привычки всех пользователей удалены", update.getMessage().getChatId());
//        }, update -> update.getMessage().getText().equals("/deleteAll"));
//    }
    /**Удаление моих привычек*/
    public Reply deleteHabits() {
        return Reply.of(update -> {
            Integer userId = update.getMessage().getFrom().getId();
            Map<Integer,String[]> newHabit = db.getMap(String.valueOf(userId));

            for (int i: newHabit.keySet()) {
                newHabit.remove(i);
            }
            silent.send("Все ваши привычки удалены", update.getMessage().getChatId());
        }, update -> update.getMessage().getText().equals("/delete"));
    }


    public static ReplyKeyboardMarkup addKeyboard(ReplyKeyboardMarkup replyKeyboardMarkup, String[] text) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        keyboard.clear();
        keyboardRow.clear();
        for (String s : text) {
            keyboardRow.add(s);
            keyboard.add(keyboardRow);
            keyboardRow = new KeyboardRow();
        }
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public Reply dHabit() {
        return Reply.of(update -> {
            silent.execute(InlineKeyboard.withButtons(update.getMessage().getFrom().getId(), db));
        }, update -> update.getMessage().getText().equals("/mark"));
    }


}
