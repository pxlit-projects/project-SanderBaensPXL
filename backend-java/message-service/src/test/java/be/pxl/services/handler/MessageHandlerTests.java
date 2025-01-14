package be.pxl.services.handler;

import be.pxl.services.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class MessageHandlerTests {

    @Mock
    private EmailService emailService;

    private MessageHandler messageHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageHandler = new MessageHandler(emailService);
    }

    @Test
    public void testHandleReviewMessageApproved() {
        String message = "12345|approved|author@example.com";

        messageHandler.handleReviewMessage(message);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService).sendEmail(emailCaptor.capture(), subjectCaptor.capture(), bodyCaptor.capture());

        assertEquals("author@example.com", emailCaptor.getValue());
        assertEquals("Post Review Update", subjectCaptor.getValue());
        assertEquals("Your post (ID: 12345) has been approved.", bodyCaptor.getValue());
    }

    @Test
    public void testHandleReviewMessageDeclined() {
        String message = "12345|declined|author@example.com";

        messageHandler.handleReviewMessage(message);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService).sendEmail(emailCaptor.capture(), subjectCaptor.capture(), bodyCaptor.capture());

        assertEquals("author@example.com", emailCaptor.getValue());
        assertEquals("Post Review Update", subjectCaptor.getValue());
        assertEquals("Your post (ID: 12345) has been declined.", bodyCaptor.getValue());
    }

    @Test
    public void testHandleReviewMessageUnknownStatus() {
        String message = "12345|unknown|author@example.com";

        messageHandler.handleReviewMessage(message);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService).sendEmail(emailCaptor.capture(), subjectCaptor.capture(), bodyCaptor.capture());

        assertEquals("author@example.com", emailCaptor.getValue());
        assertEquals("Post Review Update", subjectCaptor.getValue());
        assertEquals("There has been an update to your post (ID: 12345).", bodyCaptor.getValue());
    }
}
