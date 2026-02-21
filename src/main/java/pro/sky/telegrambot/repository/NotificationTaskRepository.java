package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с задачами напоминаний в базе данных.
 * Предоставляет методы поиска задач по статусу, времени уведомления и просроченных задач.
 */
@Repository
public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

    /**
     * Находит все задачи с указанным статусом.
     * @param status статус задачи (PENDING, SENT и т.д.)
     * @return список задач
     */
    List<NotificationTask> findByStatus(String status);

    /**
     * Находит задачи с точным временем уведомления.
     * @param time время уведомления
     * @return список задач
     */
    @Query("SELECT t FROM NotificationTask t WHERE t.notifyTime = :time")
    List<NotificationTask> findByNotifyTime(@Param("time") LocalDateTime time);

    /**
     * Находит просроченные задачи со статусом PENDING для отправки.
     * @param time текущее время (задачи с notifyTime <= time)
     * @return список задач, готовых к отправке
     */
    @Query("SELECT t FROM NotificationTask t WHERE t.notifyTime <= :time AND t.status = 'PENDING'")
    List<NotificationTask> findDueTasks(@Param("time") LocalDateTime time);
}
