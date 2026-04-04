package web.meta.wave.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.EnableScheduling;
import web.meta.wave.statements.ConfigStatements;

@Configuration
@EnableScheduling
@PropertySource("classpath:application.properties")
public class EmailConfig {
    private static final ConfigStatements configStatements = new ConfigStatements();

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Bean
    public SimpleMailMessage messageTemplate() {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setSubject(configStatements.getSubject());
        message.setFrom(fromAddress);

        return message;
    }
}
