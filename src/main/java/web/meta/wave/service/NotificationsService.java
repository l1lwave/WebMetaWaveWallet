package web.meta.wave.service;

import org.springframework.stereotype.Service;
import web.meta.wave.model.Notifications;
import web.meta.wave.repository.NotificationsRepository;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationsService {
    private final NotificationsRepository newsRepository;

    public NotificationsService(NotificationsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public void saveNotifications(Notifications notification) {
        newsRepository.save(notification);
    }

    public void deleteNotifications(Notifications notification) {
        newsRepository.delete(notification);
    }

    public Optional<Notifications> findNotificationsById(Long id) {
        return newsRepository.findById(id);
    }

    public List<Notifications> findAllNotifications() {
        return newsRepository.findAll();
    }
}
