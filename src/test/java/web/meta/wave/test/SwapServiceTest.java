package web.meta.wave.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import web.meta.wave.model.*;
import web.meta.wave.service.*;
import web.meta.wave.statements.SendStatements;
import web.meta.wave.statements.SwapStatements;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SwapServiceTest {

    @Mock
    private BalanceService balanceService;
    @Mock
    private NetworkService networkService;
    @Mock
    private TokenService tokenService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private WalletService walletService;
    @Mock
    private CustomUser customUser;
    @Mock
    private CustomUser gasUser;
    @Mock
    private MetaToken tokenFrom = new MetaToken(1L, "test1", "tt1", 1L, BigDecimal.ONE);
    @Mock
    private MetaToken tokenTo = new MetaToken(2L, "test2", "tt2", 2L, BigDecimal.TEN);
    @Mock
    private MetaToken ethToken = new MetaToken(1027L, "test3", "tt3", 3L, BigDecimal.ONE);
    @Mock
    private Network network;
    @Mock
    private Network gasNetworkk;
    @Mock
    private Balance balanceFrom;
    @Mock
    private Balance balanceTo;
    @Mock
    private Balance balanceForGas;
    @Mock
    private Balance balanceForGasAccount;
    @Mock
    private Gas gas;

    private SwapService swapService;
    private static final SwapStatements swapStatements = new SwapStatements();

    @BeforeEach
    void setUp() {
        swapService = new SwapService(balanceService, networkService, tokenService, transactionService, walletService);
    }

    @Test
    void testDoSwapSuccessfulTransfer() {
        String valueS = "100";
        String networkIdS = "1";
        String tokenFromIdS = "1";
        String tokenToIdS = "2";

        long networkId = Long.parseLong(networkIdS);
        long tokenFromId = Long.parseLong(tokenFromIdS);
        long tokenToId = Long.parseLong(tokenToIdS);
        BigDecimal value = new BigDecimal(valueS);

        when(networkService.findById(networkId)).thenReturn(Optional.of(network));
        when(tokenService.findByTokenId(tokenFromId)).thenReturn(Optional.of(tokenFrom));
        when(tokenService.findByTokenId(tokenToId)).thenReturn(Optional.of(tokenTo));
        when(walletService.getEthToken()).thenReturn(ethToken);
        when(balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network, tokenFrom, customUser)).thenReturn(Optional.of(balanceFrom));
        when(balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network, tokenTo, customUser)).thenReturn(Optional.of(balanceTo));


        BigDecimal tokenFromValue = new BigDecimal("1.0");
        BigDecimal tokenToValue = new BigDecimal("0.5");
        when(tokenFrom.getTokenValue()).thenReturn(tokenFromValue);
        when(tokenTo.getTokenValue()).thenReturn(tokenToValue);

        when(balanceFrom.getBalance()).thenReturn(new BigDecimal("200"));
        when(balanceTo.getBalance()).thenReturn(new BigDecimal("50"));

        when(walletService.doGas(network, ethToken, customUser, gasUser, gas.getSwapGas())).thenReturn(false);

        String result = swapService.doSwap(valueS, networkIdS, tokenFromIdS, tokenToIdS, customUser, gasUser);

        assertEquals(swapStatements.getNoError(), result);
    }

    @Test
    void testDoSwapInsufficientFunds() {
        String valueS = "1000";
        String networkIdS = "1";
        String tokenFromIdS = "1";
        String tokenToIdS = "2";

        long networkId = Long.parseLong(networkIdS);
        long tokenFromId = Long.parseLong(tokenFromIdS);
        long tokenToId = Long.parseLong(tokenToIdS);
        BigDecimal value = new BigDecimal(valueS);

        when(networkService.findById(networkId)).thenReturn(Optional.of(network));
        when(tokenService.findByTokenId(tokenFromId)).thenReturn(Optional.of(tokenFrom));
        when(tokenService.findByTokenId(tokenToId)).thenReturn(Optional.of(tokenTo));
        when(walletService.getEthToken()).thenReturn(ethToken);
        when(balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network, tokenFrom, customUser)).thenReturn(Optional.of(balanceFrom));
        when(balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network, tokenTo, customUser)).thenReturn(Optional.of(balanceTo));

        BigDecimal tokenFromValue = new BigDecimal("1.0");
        BigDecimal tokenToValue = new BigDecimal("0.5");
        when(tokenFrom.getTokenValue()).thenReturn(tokenFromValue);
        when(tokenTo.getTokenValue()).thenReturn(tokenToValue);

        when(balanceFrom.getBalance()).thenReturn(new BigDecimal("50"));

        when(walletService.countGas(BigDecimal.valueOf(1), tokenFrom, value, ethToken.getTokenValue())).thenReturn(BigDecimal.valueOf(0.01));

        String result = swapService.doSwap(valueS, networkIdS, tokenFromIdS, tokenToIdS, customUser, gasUser);

        assertEquals(swapStatements.getNotEnouthFundsError(), result);
    }
}

