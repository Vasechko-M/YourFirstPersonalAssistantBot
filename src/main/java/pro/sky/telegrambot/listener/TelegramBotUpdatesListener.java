package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.util.ReminderParser;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            if (update.message() != null && update.message().text() != null) {
                long chatId = update.message().chat().id();
                String messageText = update.message().text();

                if (messageText.equals("/start")) {
                    telegramBot.execute(new com.pengrad.telegrambot.request.SendMessage(chatId, "Включить напоминание?"));
                } else {
                    NotificationTask task = ReminderParser.parseMessage(messageText, chatId);
                    if (task != null) {
                        notificationTaskRepository.save(task);
                        telegramBot.execute(new com.pengrad.telegrambot.request.SendMessage(chatId, "Напоминание сохранено!"));
                    } else {
                        telegramBot.execute(new com.pengrad.telegrambot.request.SendMessage(chatId, "Не удалось распарсить сообщение. Проверьте формат."));
                    }
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
