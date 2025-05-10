package web.meta.wave.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import web.meta.wave.model.*;
import web.meta.wave.service.*;
import web.meta.wave.statements.SendStatements;

import java.math.BigDecimal;
import java.util.Optional;

public class SendServiceTest {

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
    private Network network;
    @Mock
    private MetaToken token;
    @Mock
    private MetaToken ethToken;
    @Mock
    private Balance senderBalance;
    @Mock
    private Balance recipientBalance;
    @Mock
    private CustomUser customUser;
    @Mock
    private CustomUser recipientUser;
    @Mock
    private CustomUser gasUser;

    private SendService sendService;
    private static final SendStatements sendStatements = new SendStatements();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sendService = new SendService(balanceService, networkService, tokenService, transactionService, walletService);
    }

    @Test
    public void testDoSendNotEnoughFunds() {
        String value = "1000.00";
        String networkId = "1";
        String tokenId = "1";
        BigDecimal valueBig = new BigDecimal(value);

        when(networkService.findById(Long.parseLong(networkId))).thenReturn(Optional.of(network));
        when(tokenService.findByTokenId(Long.parseLong(tokenId))).thenReturn(Optional.of(token));
        when(walletService.getEthToken()).thenReturn(ethToken);
        when(ethToken.getTokenValue()).thenReturn(BigDecimal.ONE);
        when(walletService.countGas(any(), eq(token), any(), any())).thenReturn(BigDecimal.ONE);
        when(balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network, token, customUser)).thenReturn(Optional.of(senderBalance));
        when(senderBalance.getBalance()).thenReturn(new BigDecimal("500"));

        String result = sendService.doSend(value, networkId, tokenId, customUser, recipientUser, gasUser);

        assertEquals(sendStatements.getNotEnouthFundsError(), result);
        verify(transactionService, times(1)).addTransaction(customUser, recipientUser, token, token, valueBig, valueBig, Status.FAILED);
    }

    @Test
    public void testDoSendNotEnoughGas() {
        String value = "100.00";
        String networkId = "1";
        String tokenId = "1";
        BigDecimal valueBig = new BigDecimal(value);

        when(networkService.findById(Long.parseLong(networkId))).thenReturn(Optional.of(network));
        when(tokenService.findByTokenId(Long.parseLong(tokenId))).thenReturn(Optional.of(token));
        when(walletService.getEthToken()).thenReturn(ethToken);
        when(ethToken.getTokenValue()).thenReturn(BigDecimal.ONE);
        when(walletService.countGas(any(), eq(token), any(), any())).thenReturn(BigDecimal.ONE);
        when(balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network, token, customUser)).thenReturn(Optional.of(senderBalance));
        when(balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network, token, recipientUser)).thenReturn(Optional.of(recipientBalance));
        when(senderBalance.getBalance()).thenReturn(new BigDecimal("200"));
        when(walletService.doGas(eq(network), eq(ethToken), eq(customUser), eq(gasUser), any())).thenReturn(true);

        String result = sendService.doSend(value, networkId, tokenId, customUser, recipientUser, gasUser);

        assertEquals(sendStatements.getNotEnouthGas(), result);
        verify(transactionService, times(1)).addTransaction(customUser, recipientUser, token, token, valueBig, valueBig, Status.FAILED);
    }

    @Test
    public void testDoSendErrorOnNetworkOrTokenNotFound() {
        String value = "100.00";
        String networkId = "1";
        String tokenId = "1";

        when(networkService.findById(Long.parseLong(networkId))).thenReturn(Optional.empty());

        String result = sendService.doSend(value, networkId, tokenId, customUser, recipientUser, gasUser);

        assertEquals(sendStatements.getError(), result);
    }
}

