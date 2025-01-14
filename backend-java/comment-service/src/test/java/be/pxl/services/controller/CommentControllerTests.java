package be.pxl.services.controller;

import be.pxl.services.controller.dto.CommentRequest;
import be.pxl.services.controller.dto.CommentResponse;
import be.pxl.services.services.ICommentService;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class CommentControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICommentService commentService;

    @Container
    private static final MySQLContainer sqlContainer = new MySQLContainer("mysql:5.7.37");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @Test
    void testGetComments() throws Exception {
        List<CommentResponse> comments = List.of(
                CommentResponse.builder()
                        .author("Author 1")
                        .comment("Great post!")
                        .createdDate(java.time.LocalDateTime.now())
                        .build(),
                CommentResponse.builder()
                        .author("Author 2")
                        .comment("Interesting perspective.")
                        .createdDate(java.time.LocalDateTime.now())
                        .build()
        );

        when(commentService.getComments(1L)).thenReturn(comments);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(comments)));

        verify(commentService).getComments(1L);
    }

    @Test
    void testAddComment() throws Exception {
        CommentRequest commentRequest = CommentRequest.builder()
                .comment("This is a new comment")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/1")
                        .header("X-Name", "John Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated());

        verify(commentService).addComment("John Doe", 1L, commentRequest);
    }

    @Test
    void testUpdateComment() throws Exception {
        CommentRequest commentRequest = CommentRequest.builder()
                .comment("Updated comment text")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/1/1")
                        .header("X-Name", "John Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk());

        verify(commentService).updateComment("John Doe", 1L, 1L, commentRequest);
    }

    @Test
    void testDeleteComment() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/1/1")
                        .header("X-Name", "John Doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(commentService).deleteComment("John Doe", 1L, 1L);
    }
}
