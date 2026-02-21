package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность задачи напоминания.
 * Хранит идентификатор чата, текст сообщения, время уведомления и статус.
 */
@Entity
@Table(name = "notification_task")
public class NotificationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "message_text", nullable = false, length = 255)
    private String messageText;

    @Column(name = "notify_time", nullable = false)
    private LocalDateTime notifyTime;

    @Column(length = 20)
    private String status;

    /**
     * Конструктор по умолчанию для JPA.
     */
    public NotificationTask() {
    }

    /**
     * Создаёт задачу напоминания с указанными параметрами.
     * @param chatId идентификатор чата для отправки
     * @param messageText текст напоминания
     * @param notifyTime время отправки уведомления
     * @param status статус задачи (PENDING, SENT и т.д.)
     */
    public NotificationTask(Long chatId, String messageText, LocalDateTime notifyTime, String status) {
        this.chatId = chatId;
        this.messageText = messageText;
        this.notifyTime = notifyTime;
        this.status = status;
    }

    /**
     * Возвращает уникальный идентификатор задачи.
     * @return идентификатор
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор задачи.
     * @param id идентификатор
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает идентификатор чата для отправки напоминания.
     * @return идентификатор чата
     */
    public Long getChatId() {
        return chatId;
    }

    /**
     * Устанавливает идентификатор чата.
     * @param chatId идентификатор чата
     */
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    /**
     * Возвращает текст напоминания.
     * @return текст сообщения
     */
    public String getMessageText() {
        return messageText;
    }

    /**
     * Устанавливает текст напоминания.
     * @param messageText текст сообщения
     */
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    /**
     * Возвращает время отправки уведомления.
     * @return время уведомления
     */
    public LocalDateTime getNotifyTime() {
        return notifyTime;
    }

    /**
     * Устанавливает время уведомления.
     * @param notifyTime время уведомления
     */
    public void setNotifyTime(LocalDateTime notifyTime) {
        this.notifyTime = notifyTime;
    }

    /**
     * Возвращает статус задачи.
     * @return статус (PENDING, SENT и т.д.)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Устанавливает статус задачи.
     * @param status статус
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Возвращает строковое представление задачи.
     * @return строка с полями задачи
     */
    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", messageText='" + messageText + '\'' +
                ", notifyTime=" + notifyTime +
                ", status='" + status + '\'' +
                '}';
    }
}