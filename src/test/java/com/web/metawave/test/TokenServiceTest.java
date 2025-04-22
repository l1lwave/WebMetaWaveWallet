package com.web.metawave.test;

import org.junit.jupiter.api.Test;
import web.meta.wave.model.MetaToken;
import web.meta.wave.repository.TokenRepository;
import web.meta.wave.service.CoinMarketCupService;
import web.meta.wave.service.TokenService;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenServiceTest {
    @Test
    public void testAddToken() {
        TokenRepository mockRepo = mock(TokenRepository.class);

        MetaToken token = new MetaToken(1L, "Tether", "USDT", 2L, BigDecimal.ONE);

        when(mockRepo.save(token)).thenReturn(token);

        assertEquals("USDT", token.getTokenSymbol());
    }

    @Test
    public void testFindTokenByCode() {
        TokenRepository mockRepo = mock(TokenRepository.class);
        CoinMarketCupService mockCupService = mock(CoinMarketCupService.class);
        TokenService tokenService = new TokenService(mockRepo, mockCupService);
        MetaToken token = new MetaToken(1L, "Tether", "USDT", 2L, BigDecimal.ONE);

        when(tokenService.findByTokenCode(token.getTokenCode())).thenReturn(token);

        assertEquals(token, tokenService.findByTokenCode(token.getTokenCode()));

    }
}
