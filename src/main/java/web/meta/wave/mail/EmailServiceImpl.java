package web.meta.wave.mail;

import org.springframework.context.ApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final ApplicationContext applicationContext;


    public EmailServiceImpl(JavaMailSender emailSender, ApplicationContext applicationContext) {
        this.emailSender = emailSender;
        this.applicationContext = applicationContext;
    }

    public void sendMessage(String to, String code) {
        SimpleMailMessage message = applicationContext.getBean(SimpleMailMessage.class);

        message.setText(code);
        message.setTo(to);

        emailSender.send(message);
    }
}