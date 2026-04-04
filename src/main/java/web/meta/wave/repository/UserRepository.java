package web.meta.wave.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.UserRole;

import java.util.List;

public interface UserRepository extends JpaRepository<CustomUser, Long> {
    boolean existsByEmail(String email);
    boolean existsByWalletNumber(String walletNumber);
    CustomUser findByEmail(String email);
    CustomUser findByWalletNumber(String walletNumber);
    List<CustomUser> findAllByRole(UserRole role);

    List<CustomUser> findAllByRoleAndRole(UserRole role, UserRole role1);

    boolean existsByEmailAndRole(String email, UserRole role);
}
