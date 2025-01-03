package be.pxl.services.handler;

import be.pxl.services.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageHandler {

    private final EmailService emailService;

    @RabbitListener(queues = "reviewQueue")
    public void handleReviewMessage(String message) {
        String[] parts = message.split("\\|");
        String postId = parts[0];
        String status = parts[1];
        String authorEmail = parts[2];

        String subject = "Post Review Update";
        String body = composeEmailBody(postId, status);

        emailService.sendEmail(authorEmail, subject, body);
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
