package web.meta.wave.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import web.meta.wave.model.*;
import web.meta.wave.service.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    private BalanceService balanceService;
    private NetworkService networkService;
    private TokenService tokenService;
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        balanceService = mock(BalanceService.class);
        networkService = mock(NetworkService.class);
        tokenService = mock(TokenService.class);
        walletService = new WalletService(balanceService, networkService, tokenService);
    }

    @Test
    void testDoGasNotEnoughBalance() {
        Network network = new Network();
        MetaToken ethToken = new MetaToken();
        CustomUser user = new CustomUser();
        CustomUser gasUser = new CustomUser();

        when(balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network, ethToken, user))
                .thenReturn(Optional.of(new Balance(network, ethToken, user, BigDecimal.valueOf(0.5))));

        boolean result = walletService.doGas(network, ethToken, user, gasUser, BigDecimal.ONE);

        assertTrue(result);
    }

    @Test
    void testDoGasSuccessfulTransfer() {
        Network network = new Network();
        Network gasNetwork = new Network();
        MetaToken ethToken = new MetaToken();
        CustomUser user = new CustomUser();
        CustomUser gasUser = new CustomUser();

        Balance userBalance = new Balance(network, ethToken, user, BigDecimal.valueOf(10));
        Balance gasUserBalance = new Balance(network, ethToken, gasUser, BigDecimal.valueOf(5));

        when(balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network, ethToken, user))
                .thenReturn(Optional.of(userBalance));
        when(networkService.findById(any())).thenReturn(Optional.of(gasNetwork));
        when(balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(gasNetwork, ethToken, gasUser))
                .thenReturn(Optional.of(gasUserBalance));

        boolean result = walletService.doGas(network, ethToken, user, gasUser, BigDecimal.valueOf(2));

        assertFalse(result);

        verify(balanceService).updateBalance(network, ethToken, user, BigDecimal.valueOf(8));
        verify(balanceService).updateBalance(gasNetwork, ethToken, gasUser, BigDecimal.valueOf(7));
    }

    @Test
    void testGetTokensListForNetwork() {
        Network network = new Network();
        CustomUser user = new CustomUser();

        MetaToken token1 = new MetaToken();
        MetaToken token2 = new MetaToken();

        Balance balance1 = new Balance();
        balance1.setMetaToken(token1);
        Balance balance2 = new Balance();
        balance2.setMetaToken(token2);

        when(balanceService.getBalanceByNetworkAndCustomUser(network, user))
                .thenReturn(List.of(balance1, balance2));

        List<MetaToken> tokens = walletService.getTokensListForNetwork(network, user);

        assertEquals(2, tokens.size());
        assertTrue(tokens.contains(token1));
        assertTrue(tokens.contains(token2));
    }

    @Test
    void testGetEthTokenTokenExists() {
        MetaToken ethToken = new MetaToken();
        when(tokenService.findByTokenCode(any())).thenReturn(ethToken);

        MetaToken result = walletService.getEthToken();

        assertEquals(ethToken, result);
        verify(tokenService, never()).addToken(any());
    }

    @Test
    void testGetEthTokenTokenDoesNotExist() {
        when(tokenService.findByTokenCode(any())).thenReturn(null);

        MetaToken newEthToken = new MetaToken();
        when(tokenService.findByTokenCode(any())).thenReturn(newEthToken);

        MetaToken result = walletService.getEthToken();

        assertNotNull(result);
    }

    @Test
    void testHasInvalidParams() {
        assertTrue(walletService.hasInvalidParams("valid", "", "another"));
        assertTrue(walletService.hasInvalidParams((String) null));
        assertFalse(walletService.hasInvalidParams("valid", "also valid"));
    }

    @Test
    void testCountGas() {
        MetaToken tokenFrom = new MetaToken();
        tokenFrom.setTokenValue(BigDecimal.valueOf(2));

        BigDecimal gas = BigDecimal.valueOf(5);
        BigDecimal value = BigDecimal.valueOf(100);
        BigDecimal ethValue = BigDecimal.valueOf(10);

        BigDecimal result = walletService.countGas(gas, tokenFrom, value, ethValue);

        BigDecimal expected = BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(2))
                .multiply(BigDecimal.valueOf(0.05))
                .divide(BigDecimal.valueOf(10), 30, BigDecimal.ROUND_HALF_UP);

        assertEquals(expected, result);
    }
}

