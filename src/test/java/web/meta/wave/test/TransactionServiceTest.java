package web.meta.wave.test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import web.meta.wave.model.*;
import web.meta.wave.repository.TransactionRepository;
import web.meta.wave.service.TransactionService;

import java.math.BigDecimal;
import java.util.List;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private CustomUser userFrom;
    private CustomUser userTo;
    private MetaToken tokenFrom;
    private MetaToken tokenTo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userFrom = new CustomUser();
        userTo = new CustomUser();
        tokenFrom = new MetaToken();
        tokenTo = new MetaToken();
    }

    @Test
    public void testAddTransaction() {
        BigDecimal amountFrom = BigDecimal.valueOf(100);
        BigDecimal amountTo = BigDecimal.valueOf(2.5);

        transactionService.addTransaction(userFrom, userTo, tokenFrom, tokenTo, amountFrom, amountTo, Status.CORRECT);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void testGetTransactionsbyUserFrom() {
        Transaction tx = new Transaction("2026-05-28", userFrom, userTo, tokenFrom, tokenTo, BigDecimal.ONE, BigDecimal.ONE, Status.CORRECT);
        when(transactionRepository.findAllByUserFrom(userFrom)).thenReturn(List.of(tx));

        List<Transaction> result = transactionService.getTransactionsbyUserFrom(userFrom);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(tx, result.get(0));
    }

    @Test
    public void testGetTransactionsbyUserTo() {
        Transaction tx = new Transaction("2026-05-28", userFrom, userTo, tokenFrom, tokenTo, BigDecimal.ONE, BigDecimal.ONE, Status.CORRECT);
        when(transactionRepository.findAllByUserTo(userTo)).thenReturn(List.of(tx));

        List<Transaction> result = transactionService.getTransactionsbyUserTo(userTo);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(tx, result.get(0));
    }

    @Test
    public void testGetAllTransactions() {
        Transaction tx1 = new Transaction();
        Transaction tx2 = new Transaction();
        when(transactionRepository.findAll()).thenReturn(List.of(tx1, tx2));

        List<Transaction> result = transactionService.getAllTransactions();

        assertEquals(2, result.size());
    }
}