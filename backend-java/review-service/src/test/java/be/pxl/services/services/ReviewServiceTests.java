package be.pxl.services.services;

import be.pxl.services.client.PostClient;
import be.pxl.services.controller.dto.ReviewRequest;
import be.pxl.services.controller.dto.ReviewResponse;
import be.pxl.services.domain.Review;
import be.pxl.services.exception.UnauthorizedException;
import be.pxl.services.repository.ReviewRepository;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ReviewServiceTests {

    @Container
    private static final MySQLContainer<?> sqlContainer = new MySQLContainer<>("mysql:5.7.37");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @MockBean
    private ReviewRepository reviewRepository;

    @MockBean
    private PostClient postClient;

    @MockBean
    private ReviewMessageSender messageSender;

    private ReviewService reviewService;

    private Review testReview;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(reviewRepository, postClient, messageSender);

        testReview = Review.builder()
                .id(1L)
                .author("Test Author")
                .comment("Test Comment")
                .postId(1L)
                .build();
    }

    @Test
    public void testGetReviewsUnauthorized() {
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                reviewService.getReviews("user", 1L));

        assertEquals("Admin role required", exception.getMessage());
    }

    @Test
    public void testGetReviewsSuccess() {
        when(reviewRepository.findByPostId(1L)).thenReturn(Collections.singletonList(testReview));

        List<ReviewResponse> reviews = reviewService.getReviews("admin", 1L);

        assertEquals(1, reviews.size());
        assertEquals("Test Author", reviews.get(0).getAuthor());
        assertEquals("Test Comment", reviews.get(0).getComment());
    }

    @Test
    public void testApproveReviewUnauthorized() {
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                reviewService.approveReview("user", "Test Admin", 1L));

        assertEquals("Admin role required", exception.getMessage());
    }

    @Test
    public void testApproveReviewSuccess() {
        when(postClient.getEmailByPostId(1L)).thenReturn("author@test.com");

        reviewService.approveReview("admin", "Test Admin", 1L);

        verify(postClient, times(1)).approvePost(1L, "admin", "Test Admin");
        verify(messageSender, times(1)).sendReviewMessage("1", "approved", "author@test.com");
        verify(reviewRepository, times(1)).deleteReviewsByPostId(1L);
    }

    @Test
    public void testDeclineReviewUnauthorized() {
        ReviewRequest request = new ReviewRequest("Decline Comment");

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                reviewService.declineReview("user", "Test Admin", 1L, request));

        assertEquals("Admin role required", exception.getMessage());
    }

    @Test
    public void testDeclineReviewSuccess() {
        ReviewRequest request = new ReviewRequest("Decline Comment");
        when(postClient.getEmailByPostId(1L)).thenReturn("author@test.com");

        reviewService.declineReview("admin", "Test Admin", 1L, request);

        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(captor.capture());

        Review savedReview = captor.getValue();
        assertEquals("Test Admin", savedReview.getAuthor());
        assertEquals("Decline Comment", savedReview.getComment());
        assertEquals(1L, savedReview.getPostId());

        verify(messageSender, times(1)).sendReviewMessage("1", "declined", "author@test.com");
    }
}
