package pro.sky.telegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Главный класс приложения Telegram-бота для напоминаний.
 * Запускает Spring Boot приложение с поддержкой планировщика задач.
 */
@SpringBootApplication
@EnableScheduling
public class TelegramBotApplication {

	/**
	 * Точка входа в приложение.
	 * @param args аргументы командной строки
	 */
	public static void main(String[] args) {
		SpringApplication.run(TelegramBotApplication.class, args);
	}

}
