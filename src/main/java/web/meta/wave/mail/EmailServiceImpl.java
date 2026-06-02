package web.meta.wave.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class EmailServiceImpl implements EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Override
    public void sendMessage(String to, String code) {
        try {
            String htmlContent = "<div style=\"font-family: Arial, sans-serif; background-color: #1c1e26; color: #ffffff; padding: 40px; text-align: center; border-radius: 10px; max-width: 500px; margin: auto; border: 1px solid #37474f;\">"
                    + "<h2 style=\"color: #4CAF50; margin-bottom: 5px;\">WEBMETAWAVE</h2>"
                    + "<h3 style=\"color: #ffffff; margin-top: 0;\">SECURITY SYSTEM</h3>"
                    + "<p style=\"font-size: 16px; color: #b0bec5; margin-top: 20px;\">YOU REQUESTED A VERIFICATION CODE FOR YOUR CRYPTO WALLET.</p>"
                    + "<p style=\"font-size: 14px; color: #b0bec5;\">PLEASE ENTER THE CODE BELOW TO COMPLETE YOUR ACTION:</p>"
                    + "<div style=\"margin: 30px auto; padding: 15px; background-color: #2b2e3a; border-radius: 8px; font-size: 36px; font-weight: bold; letter-spacing: 8px; color: #ffffff; max-width: 300px;\">"
                    + code
                    + "</div>"
                    + "<p style=\"font-size: 14px; color: #ef5350; margin-top: 30px;\">IF YOU DID NOT REQUEST THIS CODE, PLEASE IGNORE THIS EMAIL. YOUR ACCOUNT IS SAFE.</p>"
                    + "<hr style=\"border: 0; border-top: 1px solid #37474f; margin: 30px 0;\"/>"
                    + "<p style=\"font-size: 12px; color: #546e7a;\">&copy; 2026 WEBMETAWAVE. ALL RIGHTS RESERVED.</p>"
                    + "</div>";

            // Escape HTML for JSON
            String escapedHtml = htmlContent.replace("\"", "\\\"");

            String json = "{"
                    + "\"sender\":{\"email\":\"" + fromAddress + "\"},"
                    + "\"to\":[{\"email\":\"" + to + "\"}],"
                    + "\"subject\":\"WebMetaWave: Your Wallet Verification Code\","
                    + "\"htmlContent\":\"" + escapedHtml + "\""
                    + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("accept", "application/json")
                    .header("api-key", apiKey)
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 201) {
                System.err.println("Brevo API error: " + response.statusCode() + " " + response.body());
            }

        } catch (Exception e) {
            System.err.println("Email error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
