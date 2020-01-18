import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.*;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AnneAbilityExtension  implements AbilityExtension {

    public static final String NEW_HABIT = "newHabit";
    private static final String TABLE_USERS_ID = "usersId";
    private ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
    private SilentSender silent;
    private DBContext db;
    private List<String> habits = Arrays.asList("quit smoking", "workout", "drink water", "Add new habit");

    AnneAbilityExtension(SilentSender silent, DBContext db) {
        this.silent = silent;
        this.db = db;
        ReminderTime reminderTime = new ReminderTime(db, silent);
    }

    /** Start*/
    public Ability start() {
        return Ability
        .builder()
        .name("start")
        .locality(Locality.ALL)
        .privacy(Privacy.PUBLIC)
        .action(ctx -> {
            Map<Integer, Integer> users = db.getMap(TABLE_USERS_ID);
            if (!users.containsValue(ctx.user().getId())) {
                users.put(users.size(), ctx.user().getId());
            }
            SendMessage sendMessage = new SendMessage().setChatId(ctx.chatId());
            sendMessage.setText("Good day. This bot will help you master new good habits." +
                    "You can choose a ready-made habit or crate your own. \n\n" +
                    "If you want display your habits write /myhabits \n" +
                    "To remove all habits write /delete \n" +
                    "To mark your habit type /mark \n");
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
            addKeyboard(replyKeyboardMarkup, new String[]{"quit smoking", "workout", "drink water", "Add new habit"});
            silent.execute(sendMessage);
        })
        .build();
    }

    /**мои привычки*/
    public Ability myHabit() {
        return Ability
        .builder()
        .name("myhabits")
        .locality(Locality.ALL)
        .privacy(Privacy.PUBLIC)
        .input(0)
        .action(ctx -> {
            long userId = ctx.user().getId();
            Map <Integer, String[]> userHabits = db.getMap(String.valueOf(userId));
            String q = "";
            for (int i : userHabits.keySet()) {
                q += "Habit:  " + userHabits.get(i)[0] +
                      "\n Days:  " + userHabits.get(i)[1] +
                      "\n Time:  " + userHabits.get(i)[2] + "\n\n";
                }
            silent.send((q), ctx.chatId());
        })
        .build();
    }

    /**добавление привычки*/
    public Reply addHabit() {
        return Reply.of(update -> {
            Integer userId = update.getMessage().getFrom().getId();
            String message = update.getMessage().getText();

            Map<Integer, String[]> newHabit = db.getMap(NEW_HABIT);

            if (habits.contains(message)) {
                newHabit.put(userId, new String[]{message, null, null});
                silent.send("Write the day of the week (for example: Monday)", update.getMessage().getChatId());
            } else if (message.equals("Add new habit")) {
                newHabit.put(userId, new String[]{null, null, null});
                silent.send("Enter a name of the habit", update.getMessage().getChatId());
            } else if (newHabit.containsKey(userId)) {

                Habit habit = new Habit();
                habit.setName(newHabit.get(userId)[0]);

                if (newHabit.get(userId)[1] != null ) {
                    habit.setDayOfTheWeek(newHabit.get(userId)[1]);
                } else {
                    habit.setDayOfTheWeek(null);
                }

                if (newHabit.get(userId)[2] != null ) {
                    habit.setTime(LocalTime.parse(newHabit.get(userId)[2]));
                } else {
                    habit.setTime(null);
                }

                if (habit.getName() == null) {
                    newHabit.put(userId, new String[]{message, null, null});
                    silent.send("Write the day of the week or list (for example: Monday)", update.getMessage().getChatId());
                } else if (habit.getDayOfTheWeek() == null) {
                    newHabit.put(userId, new String[]{habit.getName(), message, null});
                    silent.send("Enter the time in the format hh:mm (for example: 03:09)", update.getMessage().getChatId());
                } else if (habit.getTime() == null) {
                    newHabit.put(userId, new String[]{habit.getName(), habit.getDayOfTheWeek(), message});

                    Map<Integer, String[]> userHabits = db.getMap(String.valueOf(userId));
                    userHabits.put(userHabits.size(), newHabit.get(userId));
                    newHabit.remove(userId);
                    silent.send("Congratulations! You made a habit!", update.getMessage().getChatId());
                }
            }
        }, Flag.TEXT,
        update -> !update.getMessage().getText().startsWith("/"));
    }

    /** ”даление всех привычек пользователей*/
    public Reply deleteAllHabits() {
        return Reply.of(update -> {
            Map<Integer, Integer> users = db.getMap(TABLE_USERS_ID);
            Map<Integer, String[]> newHabit = db.getMap("Add new habit");

            for (int i : users.keySet()) {
                newHabit.remove(i);

                Map<Integer, String[]> userHabits = db.getMap(String.valueOf(users.get(i)));

                for (int j : userHabits.keySet()) {
                    userHabits.remove(j);
                }
            }
            silent.send("\n" + "Habits of all users are extended", update.getMessage().getChatId());
        }, update -> update.getMessage().getText().equals("/deleteAll"));
    }

    /**”даление моих привычек*/
    public Ability delete() {
        return Ability
        .builder()
        .name("delete")
        .locality(Locality.ALL)
        .privacy(Privacy.PUBLIC)
        .action(ctx -> {
            Integer userId = ctx.user().getId();
            Map<Integer, String[]> newHabit = db.getMap(String.valueOf(userId));

            for (int i : newHabit.keySet()) {
                newHabit.remove(i);
            }
            silent.send("\n" + "All your habits removed", ctx.chatId());
            })
        .build();
    }

    public static ReplyKeyboardMarkup addKeyboard(ReplyKeyboardMarkup replyKeyboardMarkup, String[] text) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        for (String s : text) {
            keyboardRow.add(s);
            keyboard.add(keyboardRow);
            keyboardRow = new KeyboardRow();
        }

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public Ability dHabit() {
        return Ability
        .builder()
        .name("mark")
        .locality(Locality.ALL)
        .privacy(Privacy.PUBLIC)
        .action(ctx -> {
             silent.execute(InlineKeyboard.withButtons(ctx.user().getId(), db));
        })
        .build();
    }

    public Reply trHabit() {
        return Reply.of(update -> {
            String callBackId = update.getCallbackQuery().getData();
            Integer userId = update.getMessage().getFrom().getId();
            Map <Integer, String[]> userHabits = db.getMap(String.valueOf(userId));

            for (int i : userHabits.keySet()) {
                if (userHabits.get(i)[0].equals(callBackId)) {
                    userHabits.remove(i);
                }
            }
        }, Flag.CALLBACK_QUERY);
    }
}