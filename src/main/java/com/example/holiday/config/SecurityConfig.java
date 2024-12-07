package com.example.holiday.config;

import com.example.holiday.model.User;
import com.example.holiday.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

/**
 * Конфигурация безопасности для веб-приложения.
 * Этот класс настраивает аутентификацию и авторизацию пользователей, а также параметры для входа и выхода.
 * Использует Spring Security для защиты маршрутов и настройки безопасности приложения.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /** Сервис для управления пользователями. */
    private final UserService userService;

    /**
     * Конструктор для инициализации конфигурации безопасности с внедрением сервиса пользователей.
     *
     * @param userService сервис для работы с пользователями.
     */
    @Autowired
    public SecurityConfig(@Lazy UserService userService) {
        this.userService = userService;
    }

    /**
     * Бин для кодировщика паролей с использованием алгоритма BCrypt.
     *
     * @return объект {@link PasswordEncoder}, настроенный на BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Бин для получения пользователя по имени пользователя.
     * Используется в процессе аутентификации.
     *
     * @return объект {@link UserDetailsService}, который загружает пользователя по имени.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userService.findByUsername(username);
            if (user != null) {
                return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                        user.getRoles().stream()
                                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role.getName()))
                                .toList());
            }
            throw new RuntimeException("User not found");
        };
    }

    /**
     * Бин для менеджера аутентификации, который используется для аутентификации пользователей.
     * Конфигурирует способ аутентификации с использованием {@link UserDetailsService} и кодировщика паролей.
     *
     * @param http объект {@link HttpSecurity} для настройки безопасности.
     * @return объект {@link AuthenticationManager}.
     * @throws Exception в случае ошибки настройки.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    /**
     * Бин для конфигурации фильтров безопасности. Настроены параметры для аутентификации,
     * а также доступ к публичным маршрутам и страницам входа и выхода.
     *
     * @param http объект {@link HttpSecurity} для настройки безопасности.
     * @return объект {@link SecurityFilterChain}, который содержит настройки безопасности.
     * @throws Exception в случае ошибки настройки.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/register", "/login").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customAuthenticationSuccessHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                );
        return http.build();
    }

    /**
     * Обработчик успешного входа в систему. Перенаправляет пользователя на главную страницу после успешной аутентификации.
     *
     * @return объект {@link AuthenticationSuccessHandler}, который обрабатывает успешный вход.
     */
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                org.springframework.security.core.Authentication authentication) throws IOException {
                response.sendRedirect("/");
            }
        };
    }
}
