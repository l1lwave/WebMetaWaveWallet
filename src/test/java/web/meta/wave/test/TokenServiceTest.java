package web.meta.wave.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import web.meta.wave.model.MetaToken;
import web.meta.wave.repository.TokenRepository;
import web.meta.wave.service.CoinMarketCupService;
import web.meta.wave.service.TokenService;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenServiceTest {

    private TokenRepository tokenRepository;
    private CoinMarketCupService coinMarketCupService;
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenRepository = mock(TokenRepository.class);
        coinMarketCupService = mock(CoinMarketCupService.class);
        tokenService = new TokenService(tokenRepository, coinMarketCupService);
    }

    @Test
    void testFindAll() {
        List<MetaToken> tokens = List.of(new MetaToken());
        when(tokenRepository.findAll()).thenReturn(tokens);

        List<MetaToken> result = tokenService.findAll();

        assertEquals(tokens, result);
        verify(tokenRepository, times(1)).findAll();
    }

    @Test
    void testAddTokenNewToken() {
        long code = 123L;
        when(tokenRepository.existsByTokenCode(code)).thenReturn(false);
        when(coinMarketCupService.getTokenNameById(code)).thenReturn("Bitcoin");
        when(coinMarketCupService.getTokenSymbolById(code)).thenReturn("BTC");
        when(coinMarketCupService.getTokenRateById(code)).thenReturn(50000.0);

        boolean result = tokenService.addToken(code);

        assertTrue(result);
        verify(tokenRepository, times(1)).save(any(MetaToken.class));
    }

    @Test
    void testAddTokenAlreadyExists() {
        Long code = 123L;
        when(tokenRepository.existsByTokenCode(code)).thenReturn(true);

        boolean result = tokenService.addToken(code);

        assertFalse(result);
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void testAddTokenInvalidData() {
        Long code = 123L;
        when(tokenRepository.existsByTokenCode(code)).thenReturn(false);
        when(coinMarketCupService.getTokenNameById(code)).thenReturn(null);

        boolean result = tokenService.addToken(code);

        assertFalse(result);
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void testExistByTokenCode() {
        Long code = 123L;
        when(tokenRepository.existsByTokenCode(code)).thenReturn(true);

        boolean exists = tokenService.existByTokenCode(code);

        assertTrue(exists);
    }

    @Test
    void testFindByTokenCode() {
        Long code = 123L;
        MetaToken token = new MetaToken();
        when(tokenRepository.findByTokenCode(code)).thenReturn(token);

        MetaToken result = tokenService.findByTokenCode(code);

        assertEquals(token, result);
    }

    @Test
    void testFindByTokenId() {
        Long id = 1L;
        Optional<MetaToken> optionalToken = Optional.of(new MetaToken());
        when(tokenRepository.findById(id)).thenReturn(optionalToken);

        Optional<MetaToken> result = tokenService.findByTokenId(id);

        assertEquals(optionalToken, result);
    }

    @Test
    void testCountAll() {
        when(tokenRepository.count()).thenReturn(5L);

        Long count = tokenService.countAll();

        assertEquals(5L, count);
    }

    @Test
    void testUpdateTokenExistingToken() {
        Long code = 123L;
        MetaToken existingToken = MetaToken.builder()
                .id(1L)
                .tokenName("Old")
                .tokenSymbol("OLD")
                .tokenCode(code)
                .tokenValue(BigDecimal.valueOf(10))
                .build();

        when(tokenRepository.findByTokenCode(code)).thenReturn(existingToken);

        tokenService.updateToken("New", "NEW", code, BigDecimal.valueOf(100));

        verify(tokenRepository, times(1)).save(any(MetaToken.class));
    }

    @Test
    void testUpdateTokenNonExistingToken() {
        Long code = 123L;
        when(tokenRepository.findByTokenCode(code)).thenReturn(null);

        tokenService.updateToken("New", "NEW", code, BigDecimal.valueOf(100));

        verify(tokenRepository, never()).save(any());
    }

    @Test
    void testUpdateAllPrices() {
        MetaToken token = MetaToken.builder()
                .id(1L)
                .tokenCode(123L)
                .build();
        when(tokenRepository.findAll()).thenReturn(List.of(token));
        when(coinMarketCupService.getTokenNameById(123L)).thenReturn("NewName");
        when(coinMarketCupService.getTokenSymbolById(123L)).thenReturn("NEW");
        when(coinMarketCupService.getTokenRateById(123L)).thenReturn(1000.0);
        when(tokenRepository.findByTokenCode(123L)).thenReturn(token);

        tokenService.updateAllPrices();

        verify(tokenRepository, times(1)).save(any(MetaToken.class));
    }
}
