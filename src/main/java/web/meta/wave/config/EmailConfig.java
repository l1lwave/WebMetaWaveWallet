package web.meta.wave.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@PropertySource("classpath:application.properties")
public class EmailConfig {
    @Value("${spring.mail.username}")
    private String fromAddress;

    @Bean
    public SimpleMailMessage messageTemplate() {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setSubject("WebMetaWave Wallet code");
        message.setText("Please enter the code into form:\n\n");
        message.setFrom(fromAddress);

        return message;
    }
}
