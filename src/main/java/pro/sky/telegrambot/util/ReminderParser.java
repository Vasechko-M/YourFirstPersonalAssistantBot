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
        message = message.replace("\u00A0", " ");
        int maxAttempts = 5;
        int attempts = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        while (attempts < maxAttempts) {

            Pattern pattern = Pattern.compile(
                    "^(\\d{2}\\.\\d{2}\\.\\d{4})\\s*-\\s*(\\d{2}:\\d{2})\\s*(.*)$"
            );
            Matcher matcher = pattern.matcher(message);

            if (matcher.matches()) {

                try {
                    String datePart = matcher.group(1);
                    String timePart = matcher.group(2);
                    String taskText = matcher.group(3).trim();

                    LocalDateTime dateTime = LocalDateTime.parse(datePart + " " + timePart, formatter);
                    return new NotificationTask(chatId, taskText, dateTime, "PENDING");
                } catch (DateTimeParseException e) {
                    System.out.println("Ошибка при парсинге даты: " + e.getMessage());
                    return null;
                }
            } else {

                System.out.println("Сообщение не в правильном формате, пытаемся исправить.");

                Pattern pBetweenDateTime = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4})(\\s*)(\\d{2}:\\d{2})");
                Matcher mDT = pBetweenDateTime.matcher(message);
                if (mDT.find()) {

                    String correctedSegment = mDT.group(1) + " - " + mDT.group(3);
                    message = message.substring(0, mDT.start()) + correctedSegment + message.substring(mDT.end());
                    System.out.println("Исправленное сообщение: " + message);
                    attempts++;
                    continue;
                }

                Pattern pSeparator = Pattern.compile("^(\\d{2}\\.\\d{2}\\.\\d{4} - \\d{2}:\\d{2})(\\S+)(.*)$");
                Matcher mSep = pSeparator.matcher(message);
                if (mSep.find()) {

                    String correctedMsg = mSep.group(1) + " " + mSep.group(3).trim();
                    message = correctedMsg;
                    System.out.println("Исправленное сообщение: " + message);
                    attempts++;
                    continue;
                }

                Pattern pMissingSeparator = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4})(\\d{2}:\\d{2})");
                Matcher mMissing = pMissingSeparator.matcher(message);
                if (mMissing.find()) {

                    message = mMissing.replaceFirst("$1 - $2");
                    System.out.println("Исправленное сообщение: " + message);
                    attempts++;
                    continue;
                }
                Pattern pFlexibleSeparator = Pattern.compile(
                        "(\\d{2}\\.\\d{2}\\.\\d{4})([^\\d\\w]+)(\\d{2}:\\d{2})([^\\w]+)?(.*)"
                );
                Matcher mFlex = pFlexibleSeparator.matcher(message);

                if (mFlex.matches()) {
                    String datePart = mFlex.group(1);
                    String timePart = mFlex.group(3);
                    String taskText = mFlex.group(5).trim();

                    message = datePart + " " + timePart + " " + taskText;
                    System.out.println("Автоисправленное сообщение: " + message);
                    attempts++;
                    continue;
                }

                System.out.println("Невозможно исправить формат, выход из цикла.");
                break;
            }
        }

        System.out.println("Не удалось распарсить сообщение после " + attempts + " попыток.");
        return null;
    }
}