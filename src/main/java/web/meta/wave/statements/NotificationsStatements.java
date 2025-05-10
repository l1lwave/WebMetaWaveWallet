package web.meta.wave.statements;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NotificationsStatements {
    private final String isAdmin = "isAdmin";
    private final boolean isTrue = true;
    private final boolean isFalse = false;
    private final String notifications = "notifications";
    private final String notificationId = "notificationId";
    private final String notificationTitle = "notificationTitle";
    private final String notificationDate = "notificationDate";
    private final String notificationContent = "notificationContent";
    private final String noSuchNotification = "No such notification";

    //Windows
    private final String notificationsWindow = "notificationsWindow";
    private final String notificationInfoWindow = "notificationInfoWindow";
    private final String addNotificationWindow = "addNotificationWindow";
    private final String redirect = "redirect:/notifications";
}
