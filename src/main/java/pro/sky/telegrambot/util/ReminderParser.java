package pro.sky.telegrambot.util;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Сервис для разбора сообщений с напоминаниями.
 * Парсит текст в формате «дата время задача» и создаёт объекты NotificationTask.
 */
@Service
public class ReminderParser {

    /**
     * Шаблон: дата (dd.MM.yyyy) + любые символы + время (HH:mm) + необязательный пробел + задача.
     * Любой символ между датой, временем и задачей считается разделителем и заменяется пробелом.
     */
    private static final Pattern DATE_TIME_TASK_PATTERN = Pattern.compile(
            "^(\\d{2}\\.\\d{2}\\.\\d{4})[^\\d]*(\\d{2}:\\d{2})\\s*(.*)$",
            Pattern.DOTALL
    );

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final TelegramBot bot;

    /**
     * Создаёт парсер напоминаний с указанным экземпляром бота.
     * @param bot экземпляр TelegramBot для отправки сообщений
     */
    public ReminderParser(TelegramBot bot) {
        this.bot = bot;
    }

    /**
     * Отправляет текстовое сообщение в указанный чат.
     * @param chatId идентификатор чата
     * @param message текст сообщения
     */
    public void sendMessage(Long chatId, String message) {
        try {
            bot.execute(new SendMessage(chatId.toString(), message));
            System.out.println("Отправка в чат " + chatId + ": " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Разбирает сообщение в формате «дата время задача», где части могут быть разделены любыми символами.
     * Разделители заменяются пробелом. Дата: dd.MM.yyyy, время: HH:mm.
     * Когда наступает указанная дата/время, NotificationScheduler отправляет напоминание в чат.
     * @param message текст сообщения пользователя
     * @param chatId идентификатор чата
     * @return задача напоминания или задача на 1 минуту, если формат не распознан
     */
    public NotificationTask parseMessage(String message, Long chatId) {
        message = message.replace("\u00A0", " ").trim();

        Matcher matcher = DATE_TIME_TASK_PATTERN.matcher(message);
        if (matcher.matches()) {
            String datePart = matcher.group(1);
            String timePart = matcher.group(2);
            // Заменяем разделители между частями на пробел и нормализуем
            String taskText = matcher.group(3)
                    .replaceAll("[^\\p{L}\\p{N}\\s]", " ")
                    .replaceAll("\\s+", " ")
                    .trim();

            String dateTimeString = datePart + " " + timePart;

            try {
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
                LocalDateTime notifyTime = dateTime.truncatedTo(ChronoUnit.MINUTES);
                String task = taskText.isEmpty() ? "Сделать домашку" : taskText;

                return new NotificationTask(chatId, task, notifyTime, "PENDING");
            } catch (DateTimeParseException e) {
                // При ошибке парсинга даты — создаём задачу на 1 минуту
                String task = message.trim().isEmpty() ? "Сделать домашку" : message.trim();
                return new NotificationTask(chatId, task, LocalDateTime.now().plusMinutes(1), "PENDING");
            }
        }

        // Формат не совпадает — используем всё сообщение как задачу, напоминание через 1 минуту
        String task = message.trim().isEmpty() ? "Сделать домашку" : message.trim();
        return new NotificationTask(chatId, task, LocalDateTime.now().plusMinutes(1), "PENDING");
    }
}