package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Сервис обработки входящих сообщений с напоминаниями.
 * Парсит сообщения в формате дата.время.задача и сохраняет задачи в репозиторий.
 */
@Service
public class TaskHandlerService {

    private final NotificationTaskRepository repository;

    /**
     * Создаёт сервис с указанным репозиторием задач.
     * @param repository репозиторий для сохранения задач
     */
    public TaskHandlerService(NotificationTaskRepository repository) {
        this.repository = repository;
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    /**
     * Обрабатывает входящее сообщение: парсит дату, время и текст задачи.
     * При успешном парсинге сохраняет задачу в репозиторий.
     * @param message текст сообщения в формате дата.время.задача
     * @param chatId идентификатор чата пользователя
     * @return созданная и сохранённая задача напоминания
     */
    public NotificationTask handleIncomingMessage(String message, Long chatId) {
        message = message.replace("\u00A0", " ").trim();
        String[] parts = message.split("\\.");

        NotificationTask newTask;

        if (parts.length >= 3) {
            String datePart = parts[0].replaceAll("[^\\d\\s\\.]", " ").replaceAll("\\s+", " ").trim();
            String timePart = parts[1].replaceAll("[^\\d\\s]", " ").replaceAll("\\s+", " ").trim();
            String messagePart = parts[2].replaceAll("[^\\w\\s\\-]", " ").replaceAll("\\s+", " ").trim();

            String dateTimeString = datePart + " " + timePart;

            try {
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
                LocalDateTime scheduledTime = dateTime.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1);
                String taskText = messagePart.isEmpty() ? "Сделать домашку" : messagePart;

                newTask = new NotificationTask(chatId, taskText, scheduledTime, "PENDING");
                repository.save(newTask);
                return newTask;
            } catch (DateTimeParseException e) {
                // Ошибка парсинга — создаем задачу на 1 минуту
                newTask = new NotificationTask(chatId, "Сделать домашку", LocalDateTime.now().plusMinutes(1), "PENDING");
                repository.save(newTask);
                return newTask;
            }
        } else {
            // Формат не совпадает — создаем задачу на 1 минуту
            String taskText = message.isEmpty() ? "Сделать домашку" : message;
            newTask = new NotificationTask(chatId, taskText, LocalDateTime.now().plusMinutes(1), "PENDING");
            repository.save(newTask);
            return newTask;
        }
    }
}
