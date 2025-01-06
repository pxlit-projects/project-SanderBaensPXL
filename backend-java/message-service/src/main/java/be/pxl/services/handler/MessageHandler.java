package be.pxl.services.handler;

import be.pxl.services.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageHandler {

    private final EmailService emailService;
    private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);


    @RabbitListener(queues = "reviewQueue")
    public void handleReviewMessage(String message) {
        String[] parts = message.split("\\|");
        String postId = parts[0];
        String status = parts[1];
        String authorEmail = parts[2];

        String subject = "Post Review Update";
        String body = composeEmailBody(postId, status);

        emailService.sendEmail(authorEmail, subject, body);
        log.info("Mail sent to {}", authorEmail);
    }

    private String composeEmailBody(String postId, String status) {
        if ("approved".equals(status)) {
            return "Your post (ID: " + postId + ") has been approved.";
        } else if ("declined".equals(status)) {
            return "Your post (ID: " + postId + ") has been declined.";
        } else {
            return "There has been an update to your post (ID: " + postId + ").";
        }
    }
}
