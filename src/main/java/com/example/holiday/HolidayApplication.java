package com.example.holiday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Основной класс приложения Holiday.
 * Это точка входа в Spring Boot приложение. Класс аннотирован с помощью {@link SpringBootApplication},
 * что автоматически включает в себя настройки для компонента, конфигурации, а также запуска веб-сервера.
 */
@SpringBootApplication(scanBasePackages = "com.example.holiday")  // Указывает базовый пакет для поиска компонентов.
@EntityScan(basePackages = "com.example.holiday.model")  // Указывает пакет, в котором будут искать сущности JPA.
@EnableTransactionManagement  // Включает управление транзакциями для работы с базой данных.
public class HolidayApplication {

	/**
	 * Метод main, который запускает Spring Boot приложение.
	 *
	 * @param args аргументы командной строки
	 */
	public static void main(String[] args) {
		SpringApplication.run(HolidayApplication.class, args);
	}
}
