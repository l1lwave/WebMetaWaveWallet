package web.meta.wave.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import web.meta.wave.config.CoinMarketCupConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CoinMarketCupService {
    private final CoinMarketCupConfig coinMarketCupConfig;
    private Map<Long, JsonObject> cache = new HashMap<>();

    public CoinMarketCupService(CoinMarketCupConfig coinMarketCupConfig) {
        this.coinMarketCupConfig = coinMarketCupConfig;
    }

    public JsonObject fetchTokenInfo(long tokenId) {
        if (cache.containsKey(tokenId)) {
            return cache.get(tokenId);
        }

        try {
            URL url = new URL(coinMarketCupConfig.getApiUrl() + "?id=" + tokenId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-CMC_PRO_API_KEY", coinMarketCupConfig.getApiKey());
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + responseCode);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            reader.close();

            JsonObject json = JsonParser.parseString(jsonBuilder.toString()).getAsJsonObject();
            cache.put(tokenId, json);
            return json;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    public String getTokenNameById(long tokenId) {
        JsonObject tokenData = cache.containsKey(tokenId) ? cache.get(tokenId) : fetchTokenInfo(tokenId);
        if (tokenData == null) return null;

        try {
            JsonObject tokenInfo = tokenData.getAsJsonObject("data").getAsJsonObject(String.valueOf(tokenId));
            return tokenInfo.get("name").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public String getTokenSymbolById(long tokenId) {
        JsonObject tokenData = cache.containsKey(tokenId) ? cache.get(tokenId) : fetchTokenInfo(tokenId);
        if (tokenData == null) return null;

        try {
            JsonObject tokenInfo = tokenData.getAsJsonObject("data").getAsJsonObject(String.valueOf(tokenId));
            return tokenInfo.get("symbol").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public double getTokenRateById(long tokenId) {
        JsonObject tokenData = cache.containsKey(tokenId) ? cache.get(tokenId) : fetchTokenInfo(tokenId);
        if (tokenData == null) return -1;

        try {
            JsonObject quote = tokenData.getAsJsonObject("data")
                    .getAsJsonObject(String.valueOf(tokenId))
                    .getAsJsonObject("quote")
                    .getAsJsonObject("USD");
            return quote.get("price").getAsDouble();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Scheduled(fixedRate = 600010)
    public void updateAllPrices() {
        if (cache.isEmpty()) return;

        try {
            String idsParam = cache.keySet().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            URL url = new URL(coinMarketCupConfig.getApiUrl() + "?id=" + idsParam);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-CMC_PRO_API_KEY", coinMarketCupConfig.getApiKey());
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + responseCode);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            reader.close();

            JsonObject fullJson = JsonParser.parseString(jsonBuilder.toString()).getAsJsonObject();
            JsonObject data = fullJson.getAsJsonObject("data");

            for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
                Long id = Long.parseLong(entry.getKey());
                cache.put(id, fullJson);
            }

            System.out.println("price update");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
