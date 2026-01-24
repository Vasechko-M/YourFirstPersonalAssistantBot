package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationScheduler {

    private final NotificationTaskRepository repository;

    public NotificationScheduler(NotificationTaskRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void checkAndSendNotifications() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        System.out.println("Шедулер запущен в: " + LocalDateTime.now());
        System.out.println("Текущее время для поиска задач: " + currentTime);
        List<NotificationTask> dueTasks = findByNotifyTime(currentTime);
        System.out.println("Найдено задач: " + dueTasks.size());
        for (NotificationTask task : dueTasks) {
            System.out.println("Обработка задачи: " + task);
        }
    }

    private List<NotificationTask> findByNotifyTime(LocalDateTime dateTime) {
        return repository.findByNotifyTime(dateTime);
    }
}
