package web.meta.wave.test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import web.meta.wave.model.*;
import web.meta.wave.service.*;
import web.meta.wave.statements.BuyCryptoStatements;

import java.math.BigDecimal;
import java.util.Optional;

public class BuyCryptoServiceTest {

    @Mock
    private BalanceService balanceService;
    @Mock
    private NetworkService networkService;
    @Mock
    private TokenService tokenService;
    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private BuyCryptoService buyCryptoService;

    private static final BuyCryptoStatements buyCryptoStatements = new BuyCryptoStatements();

    private CustomUser user;
    private Network network;
    private MetaToken token;
    private Balance balance;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new CustomUser();
        network = new Network();
        network.setId(1L);

        token = new MetaToken();
        token.setId(1L);
        token.setTokenValue(BigDecimal.valueOf(40.0));

        balance = new Balance(network, token, user, BigDecimal.valueOf(5.0));
    }

    @Test
    public void testBuyCrypto_InvalidParams_ReturnsParametersError() {
        String result = buyCryptoService.buyCrypto("", "1", "1", user);
        assertEquals(buyCryptoStatements.getParametersError(), result);

        result = buyCryptoService.buyCrypto("100", null, "1", user);
        assertEquals(buyCryptoStatements.getParametersError(), result);
    }

    @Test
    public void testBuyCrypto_InvalidNumberFormat_ReturnsParametersError() {
        String result = buyCryptoService.buyCrypto("not_a_number", "1", "1", user);
        assertEquals(buyCryptoStatements.getParametersError(), result);
    }

    @Test
    public void testBuyCrypto_ZeroOrNegativeAmount_ReturnsWrongAmountError() {
        String result = buyCryptoService.buyCrypto("0", "1", "1", user);
        assertEquals(buyCryptoStatements.getWrongAmountError(), result);

        result = buyCryptoService.buyCrypto("-50", "1", "1", user);
        assertEquals(buyCryptoStatements.getWrongAmountError(), result);
    }

    @Test
    public void testBuyCrypto_NetworkOrTokenNotFound_ReturnsParametersError() {
        when(networkService.findById(1L)).thenReturn(Optional.empty());
        when(tokenService.findByTokenId(1L)).thenReturn(Optional.of(token));

        String result = buyCryptoService.buyCrypto("100", "1", "1", user);
        assertEquals(buyCryptoStatements.getParametersError(), result);
    }

    @Test
    public void testBuyCrypto_TokenValueInvalid_ReturnsTokenUnavailableError() {
        token.setTokenValue(BigDecimal.ZERO);
        when(networkService.findById(1L)).thenReturn(Optional.of(network));
        when(tokenService.findByTokenId(1L)).thenReturn(Optional.of(token));

        String result = buyCryptoService.buyCrypto("100", "1", "1", user);
        assertEquals(buyCryptoStatements.getTokenUnavailableError(), result);
    }

    @Test
    public void testBuyCrypto_BalanceNotFound_ReturnsTokenUnavailableError() {
        when(networkService.findById(1L)).thenReturn(Optional.of(network));
        when(tokenService.findByTokenId(1L)).thenReturn(Optional.of(token));
        when(balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network, token, user)).thenReturn(Optional.empty());

        String result = buyCryptoService.buyCrypto("100", "1", "1", user);
        assertEquals(buyCryptoStatements.getTokenUnavailableError(), result);
    }

    @Test
    public void testBuyCrypto_ExceptionThrown_ReturnsPrivatBankError() {
        when(networkService.findById(1L)).thenReturn(Optional.of(network));
        when(tokenService.findByTokenId(1L)).thenReturn(Optional.of(token));
        when(balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network, token, user)).thenReturn(Optional.of(balance));

        doThrow(new RuntimeException("DB Error")).when(balanceService).updateBalance(any(), any(), any(), any());

        String result = buyCryptoService.buyCrypto("100", "1", "1", user);
        assertEquals(buyCryptoStatements.getPrivatBankError(), result);
    }

    @Test
    public void testCalculateCryptoAmount() {
        BigDecimal usdAmount = new BigDecimal("100");
        BigDecimal tokenPrice = new BigDecimal("3");

        BigDecimal result = buyCryptoService.calculateCryptoAmount(usdAmount, tokenPrice);

        assertEquals(new BigDecimal("33.3333333333"), result);
    }
}