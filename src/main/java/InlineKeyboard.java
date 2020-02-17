import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InlineKeyboard {
    public static SendMessage withButtons(long userId, DBContext db) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        Map<Integer, String[]> userHabits = db.getMap(String.valueOf(userId));

        for (int i : userHabits.keySet()) {
            List <InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            keyboardButtonsRow.add(new InlineKeyboardButton(userHabits.get(i)[0]).setCallbackData(userHabits.get(i)[0]));
            rowList.add(keyboardButtonsRow);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);
        return new SendMessage().setChatId(userId).setText("Mark your habit").setReplyMarkup(inlineKeyboardMarkup);
    }
}
