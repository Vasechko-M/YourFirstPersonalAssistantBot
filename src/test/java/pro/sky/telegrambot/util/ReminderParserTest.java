package pro.sky.telegrambot.util;

import com.pengrad.telegrambot.TelegramBot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class ReminderParserTest {

    private ReminderParser reminderParser;

    @BeforeEach
    void setUp() {
        TelegramBot mockBot = mock(TelegramBot.class);
        reminderParser = new ReminderParser(mockBot);
    }

    @Test
    @DisplayName("Должен правильно распарсить сообщение в правильном формате")
    void testParseCorrectFormat() {
        String msg = "12.03.2024-14:30 Купить хлеб";
        Long chatId = 123L;

        NotificationTask task = reminderParser.parseMessage(msg, chatId);

        assertNotNull(task);
        assertEquals(chatId, task.getChatId());
        assertEquals("Купить хлеб", task.getMessageText());
        assertEquals("PENDING", task.getStatus());
        assertEquals(LocalDateTime.of(2024, 3, 12, 14, 30), task.getNotifyTime());
    }

    @Test
    @DisplayName("Должен исправить сообщение без разделителя между датой и временем")
    void testMissingSeparator() {
        String msg = "15.04.2024 16:45 Помидоры купить";
        Long chatId = 456L;

        NotificationTask task = reminderParser.parseMessage(msg, chatId);

        assertNotNull(task);
        assertEquals("Помидоры купить", task.getMessageText());
        assertEquals(LocalDateTime.of(2024, 4, 15, 16, 45), task.getNotifyTime());
    }

    @Test
    @DisplayName("Должен исправить сообщение с неправильным разделителем между датой и временем")
    void testFlexibleSeparator() {
        String message = "20.05.2024,18:00 Вечеринка";

// вызов метода парсинга

        NotificationTask task = reminderParser.parseMessage(message, 123L);

        assertNotNull(task, "Объект NotificationTask не должен быть null");

        // Проверка даты и времени
        LocalDateTime expectedDateTime = LocalDateTime.of(2024, 5, 20, 18, 0);
        assertEquals(expectedDateTime, task.getNotifyTime(), "Дата и время не совпадают");

        System.out.println("Полученное сообщение: '" + task.getMessageText() + "'");
        // Проверка текста сообщения
        assertEquals("Вечеринка", task.getMessageText(), "Текст сообщения должен быть 'Вечеринка'");

        // Проверка chatId
        assertEquals(123L, task.getChatId(), "chatId должен быть 123");
    }

    @Test
    @DisplayName("Должен вернуть задачу даже при неправильном формате сообщения")
    void testUnparseableMessage() {
        String msg = "Это неправильный формат";
        Long chatId = 101L;

        NotificationTask task = reminderParser.parseMessage(msg, chatId);

        // Проверяем, что объект не null
        assertNotNull(task);
        // Проверяем, что сообщение совпадает с вводом
        assertEquals("Это неправильный формат", task.getMessageText());
        // Время должно быть примерно текущим + 1 минута
        assertTrue(task.getNotifyTime().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    @DisplayName("Должен исправить сообщение с отсутствием пробела после даты и времени")
    void testMissingSpaceAfterDateTime() {
        String msg = "01.06.2024-10:00Сделать отчет";
        Long chatId = 202L;

        NotificationTask task = reminderParser.parseMessage(msg, chatId);

        assertNotNull(task);
        assertEquals("Сделать отчет", task.getMessageText());
        assertEquals(LocalDateTime.of(2024, 6, 1, 10, 0), task.getNotifyTime());
    }

    @Test
    @DisplayName("Должен корректно распарсить сообщение в альтернативном формате")
    void testCorrectAlternateFormat() {
        String msg = "10.07.2024 - 09:15 - Проверка системы";
        Long chatId = 303L;

        NotificationTask task = reminderParser.parseMessage(msg, chatId);

        assertNotNull(task);
        assertEquals("Проверка системы", task.getMessageText());
        assertEquals(LocalDateTime.of(2024, 7, 10, 9, 15), task.getNotifyTime());
    }

    @Test
    @DisplayName("Должен распарсить формат DD.MM.YYYY-HH:mm-Task (с дефисами)")
    void testDashSeparatedFormat() {
        String msg = "21.02.2026-19:46-Param pam pam";
        Long chatId = 500L;

        NotificationTask task = reminderParser.parseMessage(msg, chatId);

        assertNotNull(task);
        assertEquals(chatId, task.getChatId());
        assertEquals("Param pam pam", task.getMessageText());
        assertEquals(LocalDateTime.of(2026, 2, 21, 19, 46), task.getNotifyTime());
    }

    @Test
    @DisplayName("Должен корректно обработать сообщение с лишними пробелами")
    void testMessageWithExtraSpaces() {
        String msg = " 25.12.2024   -   23:59    Подготовить подарок ";
        Long chatId = 404L;

        NotificationTask task = reminderParser.parseMessage(msg, chatId);

        assertNotNull(task);
        assertEquals("Подготовить подарок", task.getMessageText());
        assertEquals(LocalDateTime.of(2024, 12, 25, 23, 59), task.getNotifyTime());
    }
}