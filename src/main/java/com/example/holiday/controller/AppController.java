package com.example.holiday.controller;

import com.example.holiday.model.User;
import com.example.holiday.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Контроллер для управления страницами авторизации, регистрации и навигации по приложению.
 * Обрабатывает запросы для публичных и защищенных страниц, обеспечивает регистрацию пользователей
 * и управление доступом.
 */
@Controller
public class AppController {

    /** Сервис для управления пользователями. */
    @Autowired
    private UserService userService;

    /**
     * Обрабатывает регистрацию нового пользователя.
     * Пользователь получает роль "ROLE_VIEWER" по умолчанию.
     *
     * @param user объект пользователя, введенный на форме регистрации.
     * @return перенаправление на страницу авторизации.
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("user") User user) {
        userService.registerUser(user, "ROLE_VIEWER");
        return "redirect:/login";
    }

    /**
     * Отображает страницу авторизации.
     *
     * @param model объект модели для передачи данных на страницу.
     * @return название представления "login".
     */
    @RequestMapping("/login")
    public String showLoginForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "login";
    }

    /**
     * Отображает главную страницу.
     *
     * @return название представления "index".
     */
    @GetMapping("/")
    public String serveHomePage() {
        return "index";
    }

    /**
     * Отображает страницу "Об авторе".
     *
     * @return название представления "about".
     */
    @GetMapping("/about")
    public String serveAboutPage() {
        return "about";
    }

    /**
     * Отображает страницу клиентов.
     *
     * @return название представления "clients".
     */
    @GetMapping("/clients")
    public String serveClientsPage() {
        return "clients";
    }

    /**
     * Отображает страницу площадок.
     *
     * @return название представления "venues".
     */
    @GetMapping("/venues")
    public String serveVenuesPage() {
        return "venues";
    }

    /**
     * Отображает страницу сотрудников (пользователей).
     * Доступно только для пользователей с ролью "ROLE_ADMIN".
     *
     * @return название представления "users".
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String serveUsersPage() {
        return "users";
    }

    /**
     * Отображает страницу мероприятий.
     *
     * @return название представления "events".
     */
    @GetMapping("/events")
    public String serveEventsPage() {
        return "events";
    }

    /**
     * Отображает страницу с диаграммами мероприятий.
     *
     * @return название представления "chart".
     */
    @GetMapping("/events/chart")
    public String serveEventsChartPage() {
        return "chart";
    }
}
