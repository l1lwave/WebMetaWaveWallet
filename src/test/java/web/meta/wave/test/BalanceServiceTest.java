package web.meta.wave.test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import web.meta.wave.model.*;
import web.meta.wave.repository.BalanceRepository;
import web.meta.wave.service.BalanceService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @InjectMocks
    private BalanceService balanceService;

    private Network network;
    private MetaToken token;
    private CustomUser user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        network = new Network();
        token = new MetaToken();
        user = new CustomUser();
    }

    @Test
    public void testAddBalanceWhenBalanceDoesNotExist() {
        when(balanceRepository.existsByNetworkAndMetaTokenAndCustomUser(network, token, user)).thenReturn(false);

        balanceService.addBalance(network, token, user, BigDecimal.valueOf(100));

        verify(balanceRepository, times(1)).save(any(Balance.class));
    }

    @Test
    public void testAddBalanceWhenBalanceExists() {
        when(balanceRepository.existsByNetworkAndMetaTokenAndCustomUser(network, token, user)).thenReturn(true);

        balanceService.addBalance(network, token, user, BigDecimal.valueOf(100));

        verify(balanceRepository, times(0)).save(any(Balance.class));
    }

    @Test
    public void testUpdateBalanceWhenBalanceExists() {
        Balance existingBalance = new Balance(network, token, user, BigDecimal.valueOf(100));
        when(balanceRepository.findByNetworkAndMetaTokenAndCustomUser(network, token, user)).thenReturn(Optional.of(existingBalance));

        balanceService.updateBalance(network, token, user, BigDecimal.valueOf(200));

        verify(balanceRepository, times(1)).save(existingBalance);
        assertEquals(BigDecimal.valueOf(200), existingBalance.getBalance());
    }

    @Test
    public void testUpdateBalanceWhenBalanceDoesNotExist() {
        when(balanceRepository.findByNetworkAndMetaTokenAndCustomUser(network, token, user)).thenReturn(Optional.empty());

        balanceService.updateBalance(network, token, user, BigDecimal.valueOf(200));

        verify(balanceRepository, times(0)).save(any(Balance.class));
    }

    @Test
    public void testGetBalanceByNetworkAndCustomUser() {
        Balance balance = new Balance(network, token, user, BigDecimal.valueOf(100));
        when(balanceRepository.findAllByNetworkAndCustomUser(network, user)).thenReturn(List.of(balance));

        List<Balance> balances = balanceService.getBalanceByNetworkAndCustomUser(network, user);

        assertFalse(balances.isEmpty());
        assertEquals(1, balances.size());
        assertEquals(balance, balances.get(0));
    }

    @Test
    public void testGetTotalBalanceNetwork() {
        Balance balance1 = new Balance(network, token, user, BigDecimal.valueOf(100));
        balance1.setMetaToken(new MetaToken());
        balance1.getMetaToken().setTokenValue(BigDecimal.valueOf(2));
        Balance balance2 = new Balance(network, token, user, BigDecimal.valueOf(150));
        balance2.setMetaToken(new MetaToken());
        balance2.getMetaToken().setTokenValue(BigDecimal.valueOf(1.5));

        when(balanceRepository.findAllByNetworkAndCustomUser(network, user)).thenReturn(List.of(balance1, balance2));

        BigDecimal totalBalance = balanceService.getTotalBalanceNetwork(network, user);

        assertEquals(BigDecimal.valueOf(425.0), totalBalance);
    }

    @Test
    public void testExistByNetworkAndMetaTokenAndUser() {
        when(balanceRepository.existsByNetworkAndMetaTokenAndCustomUser(network, token, user)).thenReturn(true);

        boolean exists = balanceService.existByNetworkAndMetaTokenAndUser(network, token, user);

        assertTrue(exists);
    }

    @Test
    public void testGetBalanceByNetworkAndMetaTokenAndCustomUser() {
        Balance balance = new Balance(network, token, user, BigDecimal.valueOf(100));
        when(balanceRepository.findByNetworkAndMetaTokenAndCustomUser(network, token, user)).thenReturn(Optional.of(balance));

        Optional<Balance> retrievedBalance = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network, token, user);

        assertTrue(retrievedBalance.isPresent());
        assertEquals(balance, retrievedBalance.get());
    }
}
