package com.web.metawave.test;

import org.junit.jupiter.api.Test;
import web.meta.wave.model.*;
import web.meta.wave.repository.TransactionRepository;
import web.meta.wave.service.TransactionService;
import web.meta.wave.service.UserService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TransactionServiceTest {
    @Test
    public void testAddTransaction() {
        TransactionRepository mockRepo = mock(TransactionRepository.class);
        UserService userService = mock(UserService.class);

        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = formatter.format(now);

        CustomUser customUser1 = new CustomUser(1L, "email1.com", "qweqwe1231eqwe", userService.generateWalletNumber(), UserRole.USER);
        CustomUser customUser2 = new CustomUser(2L, "email2.com", "qweqwe1231eqwe", userService.generateWalletNumber(), UserRole.USER);

        MetaToken token1 = new MetaToken(1L, "Tether", "USDT", 2L, BigDecimal.ONE);
        MetaToken token2 = new MetaToken(2L, "Coin", "C", 3L, BigDecimal.valueOf(100));

        Transaction transaction = new Transaction(1L, date, customUser1, customUser2, token1, token2, BigDecimal.ZERO, BigDecimal.ZERO, Status.CORRECT);

        when(mockRepo.save(transaction)).thenReturn(transaction);

        assertEquals("email1.com", transaction.getUserFrom().getEmail());
    }

    @Test
    public void testGetAllTransaction() {
        TransactionRepository mockRepo = mock(TransactionRepository.class);
        TransactionService transactionService = new TransactionService(mockRepo);
        UserService userService = mock(UserService.class);

        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = formatter.format(now);

        CustomUser customUser1 = new CustomUser(1L, "email1.com", "qweqwe1231eqwe", userService.generateWalletNumber(), UserRole.USER);
        CustomUser customUser2 = new CustomUser(2L, "email2.com", "qweqwe1231eqwe", userService.generateWalletNumber(), UserRole.USER);

        MetaToken token1 = new MetaToken(1L, "Tether", "USDT", 2L, BigDecimal.ONE);
        MetaToken token2 = new MetaToken(2L, "Coin", "C", 3L, BigDecimal.valueOf(100));

        Transaction transaction1 = new Transaction(1L, date, customUser1, customUser2, token1, token2, BigDecimal.ZERO, BigDecimal.ZERO, Status.CORRECT);
        Transaction transaction2 = new Transaction(2L, date, customUser1, customUser2, token1, token2, BigDecimal.ZERO, BigDecimal.ONE, Status.CORRECT);

        when(mockRepo.findAll()).thenReturn(Arrays.asList(transaction1, transaction2));

        List<Transaction> transactions = transactionService.getAllTransactions();
        assertEquals(2, transactions.size());
    }
}
