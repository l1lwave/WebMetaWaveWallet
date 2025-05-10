package web.meta.wave.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.meta.wave.model.Notifications;
import web.meta.wave.repository.NotificationsRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationsService {
    private final NotificationsRepository newsRepository;

    public NotificationsService(NotificationsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Transactional
    public void saveNotifications(Notifications notification) {
        newsRepository.save(notification);
    }

    @Transactional
    public void deleteNotifications(Notifications notification) {
        newsRepository.delete(notification);
    }

    @Transactional
    public Optional<Notifications> findNotificationsById(Long id) {
        return newsRepository.findById(id);
    }

    public List<Notifications> getReverseList() {
        List<Notifications> notificationsList = newsRepository.findAll();
        System.out.println(notificationsList);
        Collections.reverse(notificationsList);
        System.out.println(notificationsList);
        return notificationsList;
    }
}
