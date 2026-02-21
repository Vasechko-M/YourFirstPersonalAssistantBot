package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Сервис планировщика напоминаний.
 * Периодически проверяет задачи с наступившим временем и отправляет уведомления в Telegram.
 */
@Service
public class NotificationScheduler {

    private final NotificationTaskRepository repository;
    private final TelegramBot telegramBot;

    /**
     * Создаёт планировщик с репозиторием задач и ботом для отправки сообщений.
     * @param repository репозиторий задач напоминаний
     * @param telegramBot экземпляр TelegramBot
     */
    public NotificationScheduler(NotificationTaskRepository repository, TelegramBot telegramBot) {
        this.repository = repository;
        this.telegramBot = telegramBot;
    }

    /**
     * Проверяет задачи с наступившим временем и отправляет напоминания в чаты.
     * Выполняется каждую минуту по расписанию cron.
     */
    @Scheduled(cron = "0 0/1 * * * *")
    public void checkAndSendNotifications() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        System.out.println("Шедулер запущен в: " + LocalDateTime.now());
        System.out.println("Текущее время для поиска задач: " + currentTime);
        List<NotificationTask> dueTasks = repository.findDueTasks(currentTime);
        System.out.println("Найдено задач: " + dueTasks.size());

        for (NotificationTask task : dueTasks) {
            System.out.println("Обработка задачи: " + task);

            Long chatId = task.getChatId();
            String messageText = task.getMessageText();

            try {
                telegramBot.execute(new SendMessage(chatId, messageText));
                System.out.println("Сообщение отправлено в чат: " + chatId);
            } catch (Exception e) {
                System.err.println("Ошибка при отправке сообщения: " + e.getMessage());

                continue;
            }

            task.setStatus("SENT");
            repository.save(task);
        }
    }

}