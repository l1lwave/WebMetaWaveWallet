package web.meta.wave.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.meta.wave.model.Notifications;

@Repository
public interface NotificationsRepository extends JpaRepository<Notifications, Long> {
}
