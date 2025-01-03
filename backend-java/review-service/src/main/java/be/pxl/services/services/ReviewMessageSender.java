package be.pxl.services.services;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewMessageSender {

    private final RabbitTemplate rabbitTemplate;

    public void sendReviewMessage(String postId, String status, String authorEmail) {
        String message = postId + "|" + status + "|" + authorEmail;
        rabbitTemplate.convertAndSend("reviewQueue", message);
    }
}

