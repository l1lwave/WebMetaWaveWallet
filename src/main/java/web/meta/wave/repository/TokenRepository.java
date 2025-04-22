package web.meta.wave.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.meta.wave.model.MetaToken;

public interface TokenRepository extends JpaRepository<MetaToken, Long> {
    boolean existsByTokenCode(Long tokenCode);
    MetaToken findByTokenCode(Long tokenCode);
}
