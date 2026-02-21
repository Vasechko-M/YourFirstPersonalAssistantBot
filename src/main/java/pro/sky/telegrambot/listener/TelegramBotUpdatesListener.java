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

/**
 * Слушатель входящих обновлений Telegram-бота.
 * Обрабатывает сообщения пользователей: команду /start и создание напоминаний.
 */
@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    @Autowired
    private ReminderParser reminderParser;

    /**
     * Инициализирует слушателя после создания бина.
     * Регистрирует данный listener в боте для получения обновлений.
     */
    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    /**
     * Обрабатывает список входящих обновлений от Telegram.
     * На команду /start отвечает приветствием; остальные сообщения парсит как напоминания.
     * @param updates список обновлений
     * @return константа для подтверждения обработки всех обновлений
     */
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            if (update.message() != null && update.message().text() != null) {
                long chatId = update.message().chat().id();
                String messageText = update.message().text();

                if (messageText.equals("/start")) {
                    telegramBot.execute(new com.pengrad.telegrambot.request.SendMessage(chatId, "Включить напоминание?"));
                } else {
                    NotificationTask task = reminderParser.parseMessage(messageText, chatId);
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
