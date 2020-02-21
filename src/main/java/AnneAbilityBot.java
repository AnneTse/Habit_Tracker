import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.telegrambots.bots.DefaultBotOptions;

public class AnneAbilityBot extends AbilityBot {
    private static final String BOT_TOKEN = System.getenv("TOKEN");
    private static final String BOT_USERNAME = "HabitTrackerAnneBot";

    protected AnneAbilityBot (DefaultBotOptions botOptions) {
        super(BOT_TOKEN, BOT_USERNAME, botOptions);
    }

     public AbilityExtension ability() {
        return new AnneAbilityExtension(silent, db);
     }

    @Override
    public int creatorId() {
        return 0;
    }
}
