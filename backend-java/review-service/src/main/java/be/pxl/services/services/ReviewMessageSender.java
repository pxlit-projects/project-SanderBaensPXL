package be.pxl.services.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewMessageSender {

    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(ReviewMessageSender.class);

    public void sendReviewMessage(String postId, String status, String authorEmail) {
        String message = postId + "|" + status + "|" + authorEmail;
        rabbitTemplate.convertAndSend("reviewQueue", message);
        log.info("message sent to reviewQueue");
    }
}

