package web.meta.wave.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import web.meta.wave.config.CoinMarketCupConfig;
import web.meta.wave.service.CoinMarketCupService;

import java.lang.reflect.Field;
import java.util.Map;

public class CoinMarketCupServiceTest {

    @Mock
    private CoinMarketCupConfig coinMarketCupConfig;

    @InjectMocks
    private CoinMarketCupService coinMarketCupService;

    private JsonObject mockTokenJson;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        String jsonString = "{"
                + "\"data\": {"
                + "  \"1\": {"
                + "    \"name\": \"Bitcoin\","
                + "    \"symbol\": \"BTC\","
                + "    \"quote\": {"
                + "      \"USD\": {"
                + "        \"price\": 50000.0"
                + "      }"
                + "    }"
                + "  }"
                + "}"
                + "}";
        mockTokenJson = JsonParser.parseString(jsonString).getAsJsonObject();

        Field cacheField = CoinMarketCupService.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        Map<Long, JsonObject> cache = (Map<Long, JsonObject>) cacheField.get(coinMarketCupService);
        cache.put(1L, mockTokenJson);
    }

    @Test
    public void testGetTokenNameById_FromCache() {
        String name = coinMarketCupService.getTokenNameById(1L);
        assertEquals("Bitcoin", name);
    }

    @Test
    public void testGetTokenSymbolById_FromCache() {
        String symbol = coinMarketCupService.getTokenSymbolById(1L);
        assertEquals("BTC", symbol);
    }

    @Test
    public void testGetTokenRateById_FromCache() {
        double rate = coinMarketCupService.getTokenRateById(1L);
        assertEquals(50000.0, rate, 0.0001);
    }

    @Test
    public void testGetMethods_WhenTokenNotFoundInCacheAndFetchFails() {
        assertNull(coinMarketCupService.getTokenNameById(999L));
        assertNull(coinMarketCupService.getTokenSymbolById(999L));
        assertEquals(-1, coinMarketCupService.getTokenRateById(999L), 0.0);
    }
}