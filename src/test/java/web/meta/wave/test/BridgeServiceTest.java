package web.meta.wave.test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import web.meta.wave.model.*;
import web.meta.wave.service.*;
import web.meta.wave.statements.BridgeStatements;

import java.math.BigDecimal;
import java.util.Optional;

public class BridgeServiceTest {

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

    @InjectMocks
    private BridgeService bridgeService;

    private static final BridgeStatements bridgeStatements = new BridgeStatements();

    private CustomUser customUser;
    private CustomUser gasUser;
    private Network networkFrom;
    private Network networkTo;
    private MetaToken token;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        customUser = new CustomUser();
        gasUser = new CustomUser();

        networkFrom = new Network();
        networkFrom.setId(1L);

        networkTo = new Network();
        networkTo.setId(2L);

        token = new MetaToken();
        token.setTokenValue(new BigDecimal("1.0"));
        token.setId(1L);
    }

    @Test
    public void testDoBridgeValidParameters_Success() {
        String valueS = "10";
        String networkFromIdS = "1";
        String networkToIdS = "2";
        String tokenIdS = "1";

        Optional<Network> networkFromOptional = Optional.of(networkFrom);
        Optional<Network> networkToOptional = Optional.of(networkTo);
        Optional<MetaToken> tokenOptional = Optional.of(token);

        when(networkService.findById(1L)).thenReturn(networkFromOptional);
        when(networkService.findById(2L)).thenReturn(networkToOptional);
        when(tokenService.findByTokenId(1L)).thenReturn(tokenOptional);

        Balance balanceFrom = new Balance(networkFrom, token, customUser, new BigDecimal("100"));
        when(balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(networkFrom, token, customUser))
                .thenReturn(Optional.of(balanceFrom));

        when(walletService.hasInvalidParams(valueS, networkFromIdS, networkToIdS, tokenIdS)).thenReturn(false);
        when(walletService.getEthToken()).thenReturn(token);
        when(walletService.countGas(any(), any(), any(), any())).thenReturn(BigDecimal.ZERO);
        when(walletService.doGas(any(), any(), any(), any(), any())).thenReturn(false);

        String result = bridgeService.doBridge(valueS, networkFromIdS, networkToIdS, tokenIdS, customUser, gasUser);

        assertEquals(bridgeStatements.getNoError(), result);
    }

    @Test
    public void testDoBridgeInsufficientFunds_Failure() {
        String valueS = "200";
        String networkFromIdS = "1";
        String networkToIdS = "2";
        String tokenIdS = "1";

        Optional<Network> networkFromOptional = Optional.of(networkFrom);
        Optional<Network> networkToOptional = Optional.of(networkTo);
        Optional<MetaToken> tokenOptional = Optional.of(token);

        when(networkService.findById(1L)).thenReturn(networkFromOptional);
        when(networkService.findById(2L)).thenReturn(networkToOptional);
        when(tokenService.findByTokenId(1L)).thenReturn(tokenOptional);

        Balance balanceFrom = new Balance(networkFrom, token, customUser, new BigDecimal("100"));
        when(balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(networkFrom, token, customUser))
                .thenReturn(Optional.of(balanceFrom));

        when(walletService.hasInvalidParams(valueS, networkFromIdS, networkToIdS, tokenIdS)).thenReturn(false);
        when(walletService.getEthToken()).thenReturn(token);
        when(walletService.countGas(any(), any(), any(), any())).thenReturn(BigDecimal.ZERO);
        when(walletService.doGas(any(), any(), any(), any(), any())).thenReturn(false);

        String result = bridgeService.doBridge(valueS, networkFromIdS, networkToIdS, tokenIdS, customUser, gasUser);

        assertEquals(bridgeStatements.getNotEnouthFundsError(), result);
    }

    @Test
    public void testDoBridgeInvalidParameters_Failure() {
        String valueS = "10";
        String networkFromIdS = "1";
        String networkToIdS = "2";
        String tokenIdS = "1";

        when(walletService.hasInvalidParams(valueS, networkFromIdS, networkToIdS, tokenIdS)).thenReturn(true);

        String result = bridgeService.doBridge(valueS, networkFromIdS, networkToIdS, tokenIdS, customUser, gasUser);

        assertEquals(bridgeStatements.getParametersError(), result);
    }

    @Test
    public void testDoBridgeMinBridgeError() {
        String valueS = "0.5";
        String networkFromIdS = "1";
        String networkToIdS = "2";
        String tokenIdS = "1";

        Optional<Network> networkFromOptional = Optional.of(networkFrom);
        Optional<Network> networkToOptional = Optional.of(networkTo);
        Optional<MetaToken> tokenOptional = Optional.of(token);

        when(networkService.findById(1L)).thenReturn(networkFromOptional);
        when(networkService.findById(2L)).thenReturn(networkToOptional);
        when(tokenService.findByTokenId(1L)).thenReturn(tokenOptional);

        Balance balanceFrom = new Balance(networkFrom, token, customUser, new BigDecimal("100"));
        when(balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(networkFrom, token, customUser))
                .thenReturn(Optional.of(balanceFrom));

        when(walletService.hasInvalidParams(valueS, networkFromIdS, networkToIdS, tokenIdS)).thenReturn(false);
        when(walletService.getEthToken()).thenReturn(token);
        when(walletService.countGas(any(), any(), any(), any())).thenReturn(BigDecimal.ZERO);
        when(walletService.doGas(any(), any(), any(), any(), any())).thenReturn(false);

        String result = bridgeService.doBridge(valueS, networkFromIdS, networkToIdS, tokenIdS, customUser, gasUser);

        assertEquals(bridgeStatements.getMinBridgeError(), result);
    }
}
