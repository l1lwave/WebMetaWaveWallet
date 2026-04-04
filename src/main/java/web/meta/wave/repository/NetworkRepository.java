package web.meta.wave.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.meta.wave.model.Network;

public interface NetworkRepository extends JpaRepository<Network, Long> {
    boolean existsByName(String name);
}
