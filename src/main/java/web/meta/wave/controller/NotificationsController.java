package web.meta.wave.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.Notifications;
import web.meta.wave.service.NotificationsService;
import web.meta.wave.service.UserService;
import web.meta.wave.statements.NotificationsStatements;

import java.util.List;
import java.util.Optional;

@Controller
public class NotificationsController {
    private final NotificationsService notificationsService;
    private final UserService userService;

    private static final NotificationsStatements notificationsStatements = new NotificationsStatements();

    public NotificationsController(NotificationsService notificationsService, UserService userService) {
        this.notificationsService = notificationsService;
        this.userService = userService;
    }

    @GetMapping("/notifications")
    public String notifications(Model model) {
        List<Notifications> notificationsList = notificationsService.getReverseList();
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        if (userService.isAdmin(customUser))
            model.addAttribute(notificationsStatements.getIsAdmin(), notificationsStatements.isTrue());

        model.addAttribute(notificationsStatements.getNotifications(), notificationsList);
        return notificationsStatements.getNotificationsWindow();
    }

    @GetMapping("/notifications/{notificationId}")
    public String otificationDetail(@PathVariable("notificationId") long notificationId, Model model) {
        Optional<Notifications> notificationInfo = notificationsService.findNotificationsById(notificationId);
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        if (userService.isAdmin(customUser))
            model.addAttribute(notificationsStatements.getIsAdmin(), notificationsStatements.isTrue());

        notificationInfo.ifPresentOrElse(notification -> {
            model.addAttribute(notificationsStatements.getNotificationId(), notification.getId());
            model.addAttribute(notificationsStatements.getNotificationTitle(), notification.getTitle());
            model.addAttribute(notificationsStatements.getNotificationDate(), notification.getDate());
            model.addAttribute(notificationsStatements.getNotificationContent(), notification.getContent());
        }, () -> {
            model.addAttribute(notificationsStatements.getNotificationTitle(), notificationsStatements.getNoSuchNotification());
        });
        return notificationsStatements.getNotificationInfoWindow();
    }

    @GetMapping("/deleteNotification={notificationId}")
    public String deleteNew(@PathVariable("notificationId") long notificationId) {
        Optional<Notifications> notificationInfo = notificationsService.findNotificationsById(notificationId);
        notificationInfo.ifPresent(notificationsService::deleteNotifications);
        return notificationsStatements.getRedirect();
    }

    @GetMapping("/addNotification")
    public String addNotification() {
        return notificationsStatements.getAddNotificationWindow();
    }

    @PostMapping(value = "/addNewNotification")
    public String addNewNotification(@ModelAttribute("title") String title,
                                     @ModelAttribute("content") String content,
                                     Model model) {

        Notifications notification = new Notifications(title, content);
        notificationsService.saveNotifications(notification);
        return notificationsStatements.getRedirect();
    }
}
