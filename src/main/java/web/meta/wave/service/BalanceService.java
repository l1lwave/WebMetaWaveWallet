package web.meta.wave.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.meta.wave.model.Balance;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.MetaToken;
import web.meta.wave.model.Network;
import web.meta.wave.repository.BalanceRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BalanceService {
    private final BalanceRepository balanceRepository;
    public BalanceService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @Transactional
    public void addBalance(Network network, MetaToken token, CustomUser user, BigDecimal amount) {
        if(balanceRepository.existsByNetworkAndMetaTokenAndCustomUser(network, token, user)) {
            return;
        }
        balanceRepository.save(new Balance(network, token, user, amount));
    }

    @Transactional
    public void updateBalance(Network network, MetaToken token, CustomUser user, BigDecimal amount) {
        Optional<Balance> balance = balanceRepository.findByNetworkAndMetaTokenAndCustomUser(network, token, user);
        if(balance.isPresent()) {
            Balance b = balance.get();
            b.setBalance(amount);
            balanceRepository.save(b);
        }
    }

    @Transactional(readOnly = true)
    public List<Balance> getBalanceByNetworkAndCustomUser(Network network, CustomUser user) {
        return balanceRepository.findAllByNetworkAndCustomUser(network, user);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalBalanceNetwork(Network network, CustomUser user) {
        List<Balance> balances = getBalanceByNetworkAndCustomUser(network, user);
        BigDecimal total = BigDecimal.ZERO;
        if(!balances.isEmpty()) {
            for (Balance b : balances) {
                total = total.add(

                        b.getBalance().multiply(
                                b.getMetaToken().getTokenValue()
                        )
                );
            }
            return total;
        }
        return total;
    }

    @Transactional(readOnly = true)
    public boolean existByNetworkAndMetaTokenAndUser(Network network, MetaToken token, CustomUser user) {
        return balanceRepository.existsByNetworkAndMetaTokenAndCustomUser(network, token, user);
    }

    @Transactional(readOnly = true)
    public Optional<Balance> getBalanceByNetworkAndMetaTokenAndCustomUser(Network network, MetaToken token, CustomUser user) {
        return balanceRepository.findByNetworkAndMetaTokenAndCustomUser(network, token, user);
    }
}
