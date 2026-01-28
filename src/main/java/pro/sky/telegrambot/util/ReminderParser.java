package pro.sky.telegrambot.util;

import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReminderParser {

    public static NotificationTask parseMessage(String message, Long chatId) {
        System.out.println("Обработка сообщения: [" + message + "]");
        message = message.replace("\u00A0", " ").trim();
        int maxAttempts = 5;
        int attempts = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String originalMessage = message;

        while (attempts < maxAttempts) {
            Pattern patternDash = Pattern.compile(
                    "^(\\d{2}\\.\\d{2}\\.\\d{4})-(\\d{2}:\\d{2})-?(.*)$"
            );
            Matcher matcherDash = patternDash.matcher(message);
            if (matcherDash.matches()) {
                try {
                    String datePart = matcherDash.group(1);
                    String timePart = matcherDash.group(2);
                    String taskText = matcherDash.group(3).trim();

                    LocalDateTime dateTime = LocalDateTime.parse(datePart + " " + timePart, formatter);
                    String taskOnly = taskText.isEmpty() ? "Сделать домашку" : taskText;
                    return new NotificationTask(chatId, taskOnly, dateTime, "PENDING");
                } catch (DateTimeParseException e) {
                    System.out.println("Ошибка при парсинге даты: " + e.getMessage());
                    return new NotificationTask(chatId, "Сделать домашку", LocalDateTime.now().plusMinutes(1), "PENDING");
                }
            }

            Pattern patternComma = Pattern.compile(
                    "^(\\d{2}\\.\\d{2}\\.\\d{4}),?(\\d{2}:\\d{2})-?(.*)$"
            );
            Matcher matcherComma = patternComma.matcher(message);
            if (matcherComma.matches()) {
                try {
                    String datePart = matcherComma.group(1);
                    String timePart = matcherComma.group(2);
                    String taskText = matcherComma.group(3).trim();

                    LocalDateTime dateTime = LocalDateTime.parse(datePart + " " + timePart, formatter);
                    String taskOnly = taskText.isEmpty() ? "Сделать домашку" : taskText;
                    return new NotificationTask(chatId, taskOnly, dateTime, "PENDING");
                } catch (DateTimeParseException e) {
                    System.out.println("Ошибка при парсинге даты: " + e.getMessage());
                    return new NotificationTask(chatId, "Сделать домашку", LocalDateTime.now().plusMinutes(1), "PENDING");
                }
            }

            Pattern patternFixSep = Pattern.compile("^(\\d{2}\\.\\d{2}\\.\\d{4})(\\d{2}:\\d{2})(.*)$");
            Matcher mFixSep = patternFixSep.matcher(message);
            if (mFixSep.matches()) {
                message = mFixSep.group(1) + " " + mFixSep.group(2) + " " + mFixSep.group(3).trim();
                attempts++;
                continue;
            }

            Pattern patternFixCommaDash = Pattern.compile("^(\\d{2}\\.\\d{2}\\.\\d{4})(\\s*)(\\d{2}:\\d{2})(.*)$");
            Matcher mFixCommaDash = patternFixCommaDash.matcher(message);
            if (mFixCommaDash.matches()) {
                message = mFixCommaDash.group(1) + " " + mFixCommaDash.group(3) + " " + mFixCommaDash.group(4).trim();
                attempts++;
                continue;
            }

            System.out.println("Формат неподдерживаемый. Используем всё сообщение как задачу.");
            String taskOnly = message.trim().isEmpty() ? "Сделать домашку" : message.trim();
            return new NotificationTask(chatId, taskOnly, LocalDateTime.now().plusMinutes(1), "PENDING");
        }

        System.out.println("Не удалось распарсить сообщение после " + attempts + " попыток.");
        String taskOnly = message.trim().isEmpty() ? "Сделать домашку" : message.trim();
        return new NotificationTask(chatId, taskOnly, LocalDateTime.now().plusMinutes(1), "PENDING");
    }
}