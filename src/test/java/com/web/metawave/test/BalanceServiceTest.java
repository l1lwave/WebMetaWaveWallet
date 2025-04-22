package com.web.metawave.test;

import org.junit.jupiter.api.Test;
import web.meta.wave.model.*;
import web.meta.wave.repository.BalanceRepository;
import web.meta.wave.service.BalanceService;
import web.meta.wave.service.UserService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BalanceServiceTest {
    @Test
    public void testAddBalance() {
        BalanceRepository mockRepo = mock(BalanceRepository.class);
        UserService userService = mock(UserService.class);

        CustomUser customUser = new CustomUser(1L, "email1.com", "qweqwe1231eqwe", userService.generateWalletNumber(), UserRole.USER);
        MetaToken token = new MetaToken(1L, "Tether", "USDT", 2L, BigDecimal.ONE);
        Network network = new Network(1L, "TestNetwork");

        Balance balance = new Balance(1L, network, token, customUser, BigDecimal.valueOf(10));

        when(mockRepo.save(balance)).thenReturn(balance);

        assertEquals(customUser, balance.getCustomUser());
    }

    @Test
    public void testUpdateBalance() {
        BalanceRepository mockRepo = mock(BalanceRepository.class);
        BalanceService balanceService = new BalanceService(mockRepo);
        UserService userService = mock(UserService.class);

        CustomUser customUser = new CustomUser(1L, "email1.com", "qweqwe1231eqwe", userService.generateWalletNumber(), UserRole.USER);
        MetaToken token = new MetaToken(1L, "Tether", "USDT", 2L, BigDecimal.ONE);
        Network network = new Network(1L, "TestNetwork");
        Balance balance = new Balance(1L, network, token, customUser, BigDecimal.valueOf(10));

        when(mockRepo.findByNetworkAndMetaTokenAndCustomUser(network, token, customUser)).thenReturn(Optional.of(balance));

        balanceService.updateBalance(network, token, customUser, BigDecimal.valueOf(100));
        assertEquals(BigDecimal.valueOf(100), balance.getBalance());

    }

    @Test
    public void testGetBalance() {
        BalanceRepository mockRepo = mock(BalanceRepository.class);
        BalanceService balanceService = new BalanceService(mockRepo);
        UserService userService = mock(UserService.class);

        CustomUser customUser = new CustomUser(1L, "email1.com", "qweqwe1231eqwe", userService.generateWalletNumber(), UserRole.USER);
        MetaToken token1 = new MetaToken(1L, "Tether", "USDT", 2L, BigDecimal.ONE);
        MetaToken token2 = new MetaToken(2L, "Test", "TEST", 1L, BigDecimal.TEN);

        Network network = new Network(1L, "TestNetwork");

        Balance balance1 = new Balance(1L, network, token1, customUser, BigDecimal.valueOf(10));
        Balance balance2 = new Balance(2L, network, token2, customUser, BigDecimal.valueOf(20));

        when(mockRepo.findAllByNetworkAndCustomUser(network, customUser)).thenReturn(Arrays.asList(balance1, balance2));

        List<Balance> balances = balanceService.getBalanceByNetworkAndCustomUser(network, customUser);
        assertEquals(2, balances.size());
    }
};


