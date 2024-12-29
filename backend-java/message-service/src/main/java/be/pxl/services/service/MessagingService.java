package be.pxl.services.service;

import be.pxl.services.dto.EmailRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagingService {

    private final JavaMailSender mailSender;

    @RabbitListener(queues = "post-notification-queue")
    public void handlePostNotification(EmailRequest emailRequest) {
        try {
            sendEmail(emailRequest);
        } catch (Exception e) {
            System.err.println("Failed to process email request: " + e.getMessage());
        }
    }

    public void sendEmail(EmailRequest emailRequest) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("sanderbaens@gmail.com");
            helper.setTo(emailRequest.getEmail());
            helper.setSubject(emailRequest.getSubject());
            helper.setText(emailRequest.getBody(), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
