package pro.sky.telegrambot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Telegram-бота.
 * Создаёт и настраивает экземпляр бота по токену из настроек приложения.
 */
@Configuration
public class TelegramBotConfiguration {

    @Value("${telegram.bot.token}")
    private String token;

    /**
     * Создаёт и возвращает экземпляр Telegram-бота.
     * Удаляет ранее установленные команды бота при инициализации.
     * @return настроенный экземпляр TelegramBot
     */
    @Bean
    public TelegramBot telegramBot() {
        TelegramBot bot = new TelegramBot(token);
        bot.execute(new DeleteMyCommands());
        return bot;
    }

}
