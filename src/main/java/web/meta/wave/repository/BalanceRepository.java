package web.meta.wave.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.meta.wave.model.Balance;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.MetaToken;
import web.meta.wave.model.Network;

import java.util.List;
import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
    boolean existsByNetworkAndMetaTokenAndCustomUser(Network network, MetaToken metaToken, CustomUser customUser);

    Optional<Balance> findByNetworkAndMetaTokenAndCustomUser(Network network, MetaToken metaToken, CustomUser customUser);

    List<Balance> findAllByCustomUser(CustomUser customUser);

    List<Balance> findAllByNetworkAndCustomUser(Network network, CustomUser customUser);
}
