package pro.sky.telegrambot.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Конфигурация планировщика задач.
 * Включает поддержку аннотации @Scheduled для периодического выполнения задач.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
