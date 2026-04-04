package web.meta.wave.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class LiqPayService {

    @Value("${liqpay.public.key}")
    private String publicKey;

    @Value("${liqpay.private.key}")
    private String privateKey;

    public String generatePaymentForm(double amount, String currency, String networkId, String tokenId, String baseUrl) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("public_key", publicKey);
        params.put("version", "3");
        params.put("action", "pay");
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("description", "BUY TOKEN " + tokenId + " IN NETWORK " + networkId);
        params.put("order_id", "ORDER_" + UUID.randomUUID().toString());
        params.put("sandbox", "1");

        String resultUrl = baseUrl + "/payment-success?amountUsdS=" + amount + "&networkIdS=" + networkId + "&tokenIdS=" + tokenId;
        params.put("result_url", resultUrl);

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(params);

        String data = Base64.getEncoder().encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));

        String signString = privateKey + data + privateKey;
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] sha1Hash = md.digest(signString.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getEncoder().encodeToString(sha1Hash);

        return "<html><body style='display:flex; justify-content:center; align-items:center; height:100vh; background-color:#1c1e26; color:white; font-family:sans-serif;'>" +
                "<h2>REDIRECTING TO LIQPAY...</h2>" +
                "<form id='liqpay_form' method='POST' action='https://www.liqpay.ua/api/3/checkout' accept-charset='utf-8'>" +
                "<input type='hidden' name='data' value='" + data + "'/>" +
                "<input type='hidden' name='signature' value='" + signature + "'/>" +
                "</form>" +
                "<script>setTimeout(function() { document.getElementById('liqpay_form').submit(); }, 1000);</script>" +
                "</body></html>";
    }
}