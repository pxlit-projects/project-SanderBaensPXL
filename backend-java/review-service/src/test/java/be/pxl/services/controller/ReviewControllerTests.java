package be.pxl.services.controller;

import be.pxl.services.controller.dto.ReviewRequest;
import be.pxl.services.controller.dto.ReviewResponse;
import be.pxl.services.services.IReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ReviewControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IReviewService reviewService;

    @Container
    private static final MySQLContainer sqlContainer = new MySQLContainer("mysql:5.7.37");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @Test
    void testGetReviews() throws Exception {
        List<ReviewResponse> reviews = List.of(
                ReviewResponse.builder()
                        .author("Author 1")
                        .comment("Great post!")
                        .build(),
                ReviewResponse.builder()
                        .author("Author 2")
                        .comment("Interesting perspective.")
                        .build()
        );

        when(reviewService.getReviews("admin", 1L)).thenReturn(reviews);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/1")
                        .header("X-Role", "admin")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(reviews)));

        verify(reviewService).getReviews("admin", 1L);
    }

    @Test
    void testApproveReview() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/approve/1")
                        .header("X-Role", "admin")
                        .header("X-Name", "John Doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(reviewService).approveReview("admin", "John Doe", 1L);
    }

    @Test
    void testDeclineReview() throws Exception {
        ReviewRequest reviewRequest = ReviewRequest.builder()
                .comment("Not relevant")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/decline/1")
                        .header("X-Role", "admin")
                        .header("X-Name", "John Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isCreated());

        verify(reviewService).declineReview("admin", "John Doe", 1L, reviewRequest);
    }
}
