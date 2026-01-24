package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repositiry.NotificationTaskRepository;
import pro.sky.telegrambot.utilty.ReminderParser;

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
//            logger.info("Processing update: {}", update);
//            if (update.message() != null) {
//                String messageText = update.message().text();
//                if (messageText != null && messageText.equals("/start")) {
//                    long chatId = update.message().chat().id();
//
//                    //telegramBot.execute(new com.pengrad.telegrambot.request.SendMessage(chatId, "Включить напоминание?"));
//                    SendResponse response = telegramBot.execute(new com.pengrad.telegrambot.request.SendMessage(chatId, "Включить напоминание?"));
//                    logger.info("Sent message response: {}", response);
//                    if (response.isOk()) {
//                        logger.info("Message sent successfully");
//                    } else {
//                        logger.error("Error sending: {}", response.description());
//                    }
//                }
//            }
//        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
