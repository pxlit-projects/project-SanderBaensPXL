package be.pxl.services.controller;

import be.pxl.services.controller.dto.PostRequest;
import be.pxl.services.controller.dto.PostResponse;
import be.pxl.services.services.IPostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class PostControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IPostService postService;

    @Container
    private static final MySQLContainer sqlContainer = new MySQLContainer("mysql:5.7.37");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @Test
    void testAddPost() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .title("New Post")
                .content("This is a new post.")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Role", "admin")
                        .header("X-Name", "John Doe")
                        .header("X-Email", "john.doe@example.com")
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isCreated());

        verify(postService).addPost("admin", "John Doe", "john.doe@example.com", postRequest);
    }

    @Test
    void testGetUnacceptedPost() throws Exception {
        List<PostResponse> unacceptedPosts = List.of(
                PostResponse.builder()
                        .id(1L)
                        .title("Post 1")
                        .content("Content 1")
                        .author("Author 1")
                        .createdDate(LocalDateTime.now())
                        .accepted(false)
                        .build()
        );

        when(postService.findUnaccepted("admin")).thenReturn(unacceptedPosts);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/unaccepted")
                        .header("X-Role", "admin")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(unacceptedPosts)));

        verify(postService).findUnaccepted("admin");
    }

    @Test
    void testGetAcceptedPost() throws Exception {
        List<PostResponse> acceptedPosts = List.of(
                PostResponse.builder()
                        .id(2L)
                        .title("Post 2")
                        .content("Content 2")
                        .author("Author 2")
                        .createdDate(LocalDateTime.now())
                        .accepted(true)
                        .build()
        );

        when(postService.findAccepted()).thenReturn(acceptedPosts);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/accepted")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(acceptedPosts)));

        verify(postService).findAccepted();
    }

    @Test
    void testGetPostById() throws Exception {
        PostResponse postResponse = PostResponse.builder()
                .id(1L)
                .title("Test Post")
                .content("This is a test post.")
                .author("Author 1")
                .createdDate(LocalDateTime.now())
                .accepted(true)
                .build();

        when(postService.findPostById(1L)).thenReturn(postResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(postResponse)));

        verify(postService).findPostById(1L);
    }

    @Test
    void testApprovePost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/approve/1")
                        .header("X-Role", "admin")
                        .header("X-Name", "John Doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(postService).approvePost("admin", "John Doe", 1L);
    }

    @Test
    void testGetEmailByPostId() throws Exception {
        when(postService.getEmailByPostId(1L)).thenReturn("author@example.com");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/1/email")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("author@example.com"));

        verify(postService).getEmailByPostId(1L);
    }
}

