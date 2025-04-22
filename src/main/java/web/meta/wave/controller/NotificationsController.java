package web.meta.wave.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.Notifications;
import web.meta.wave.model.UserRole;
import web.meta.wave.service.NotificationsService;
import web.meta.wave.service.UserService;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class NotificationsController {
    private final NotificationsService notificationsService;
    private final UserService userService;

    public NotificationsController(NotificationsService notificationsService, UserService userService) {
        this.notificationsService = notificationsService;
        this.userService = userService;
    }

    @GetMapping("/notifications")
    public String notifications(Model model) {
        List<Notifications> notificationsList = notificationsService.findAllNotifications();
        CustomUser user = userService.findByEmail(userService.getCurrentUser().getUsername());
        if (user.getRole().equals(UserRole.ADMIN)) {
            model.addAttribute("isAdmin", true);
        }
        Collections.reverse(notificationsList);
        model.addAttribute("notifications", notificationsList);
        return "notificationsWindow";
    }

    @GetMapping("/notifications/{notificationId}")
    public String otificationDetail(@PathVariable("notificationId") long notificationId, Model model) {
        Optional<Notifications> notificationInfo = notificationsService.findNotificationsById(notificationId);
        CustomUser user = userService.findByEmail(userService.getCurrentUser().getUsername());
        if (user.getRole().equals(UserRole.ADMIN)) {
            model.addAttribute("isAdmin", true);
        }
        if (notificationInfo.isPresent()) {
            model.addAttribute("notificationId", notificationInfo.get().getId());
            model.addAttribute("notificationTitle", notificationInfo.get().getTitle());
            model.addAttribute("notificationDate", notificationInfo.get().getDate());
            model.addAttribute("notificationContent", notificationInfo.get().getContent());
        } else {
            model.addAttribute("notificationTitle", "No such notification");
        }

        return "notificationInfoWindow";
    }

    @GetMapping("/deleteNotification={notificationId}")
    public String deleteNew(@PathVariable("notificationId") long notificationId) {
        Optional<Notifications> notificationInfo = notificationsService.findNotificationsById(notificationId);
        notificationInfo.ifPresent(notificationsService::deleteNotifications);
        return "redirect:/notifications";
    }

    @GetMapping("/addNotification")
    public String addNotification() {
        return "addNotificationWindow";
    }

    @PostMapping(value = "/addNewNotification")
    public String addNewNotification(@ModelAttribute("title") String title,
                                     @ModelAttribute("content") String content,
                                     Model model) {

        Notifications notification = new Notifications(title, content);
        notificationsService.saveNotifications(notification);
        return "redirect:/notifications";
    }
}
