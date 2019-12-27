import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;

public class AnneAbilityBot extends AbilityBot {
    private static final String BOT_TOKEN = System.getenv("TOKEN");
    private static final String BOT_USERNAME = "HabitTrackerAnneBot";
    private ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

    protected AnneAbilityBot (DefaultBotOptions botOptions) {
        super(BOT_TOKEN, BOT_USERNAME, botOptions);
    }

    public Ability Start() {
        return Ability
                .builder()
                .name("start")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    SendMessage sendMessage = new
                            SendMessage().setChatId(ctx.chatId());
                    sendMessage.setText("select");
                    sendMessage.setReplyMarkup(replyKeyboardMarkup);
                    addKeyboard(replyKeyboardMarkup, new String[]{"My habit", "Select new habit", "Add new habit"});
                    silent.execute(sendMessage);
                })
                .build();
    }

    public Ability Add() {
        return Ability
                .builder()
                .name("add")
                .input(1)
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx  -> {
                    String valueName = ctx.firstArg();
                    Habit habit = new Habit(db);
                    habit.addHabit(valueName);
                    silent.send("add", ctx.chatId());
                })
                .build();
    }

    public Reply MyHabit() {
        return Reply.of(update -> {
            Habit habit = new Habit(db);
            silent.send(Arrays.toString(habit.get()), update.getMessage().getChatId());
            addKeyboard(replyKeyboardMarkup, habit.get());
        }, update -> update.getMessage().getText().equals("My habit"));
    }

    public Reply selectNew() {
        return Reply.of(update -> {
            //addKeyboard(replyKeyboardMarkup, new String[]{"habit1", "habit2", "habit3", "habit4"});
        }, update -> update.getMessage().getText().equals("Select new habit"));
    }

    public Reply addNew() {
        return Reply.of(update -> {
            silent.send("Для добавления новой привычуи введте ее в формате /add Название_привычки", update.getMessage().getChatId());
        }, update -> update.getMessage().getText().equals("Add new habit"));
    }

    public Reply delete() {
        return Reply.of(update -> {
            Habit habit = new Habit(db);
            habit.remove();
            silent.send("delete All", update.getMessage().getChatId());
        }, update -> update.getMessage().getText().equals("/delete"));
    }

    public static void addKeyboard(ReplyKeyboardMarkup replyKeyboardMarkup,String[] text) {
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
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
    }
    @Override
    public int creatorId() {
        return 123456;
    }
}
