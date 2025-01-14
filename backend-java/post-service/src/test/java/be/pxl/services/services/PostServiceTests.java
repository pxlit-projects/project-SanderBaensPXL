package be.pxl.services.services;

import be.pxl.services.controller.dto.PostRequest;
import be.pxl.services.controller.dto.PostResponse;
import be.pxl.services.domain.Post;
import be.pxl.services.exception.UnauthorizedException;
import be.pxl.services.repository.PostRepository;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class PostServiceTests {

    @Container
    private static final MySQLContainer<?> sqlContainer = new MySQLContainer<>("mysql:5.7.37");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @MockBean
    private PostRepository postRepository;

    private PostService postService;

    private Post testPost;

    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository);

        testPost = Post.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .author("Test Author")
                .email("author@test.com")
                .createdDate(LocalDateTime.now())
                .accepted(false)
                .build();
    }

    @Test
    public void testAddPostUnauthorized() {
        PostRequest request = new PostRequest("New Title", "New Content");

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                postService.addPost("user", "Test Author", "author@test.com", request));

        assertEquals("Admin role required", exception.getMessage());
    }

    @Test
    public void testAddPostSuccess() {
        PostRequest request = new PostRequest("New Title", "New Content");

        postService.addPost("admin", "Test Author", "author@test.com", request);

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());

        Post savedPost = captor.getValue();
        assertEquals("New Title", savedPost.getTitle());
        assertEquals("Test Author", savedPost.getAuthor());
        assertFalse(savedPost.isAccepted());
    }

    @Test
    public void testFindUnacceptedPosts() {
        when(postRepository.findPostsByAccepted(false)).thenReturn(Collections.singletonList(testPost));

        var unacceptedPosts = postService.findUnaccepted("admin");

        assertEquals(1, unacceptedPosts.size());
        assertEquals("Test Title", unacceptedPosts.get(0).getTitle());
    }

    @Test
    public void testFindPostById() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        var post = postService.findPostById(1L);

        assertEquals("Test Title", post.getTitle());
        assertEquals("Test Author", post.getAuthor());
    }

    @Test
    public void testApprovePostUnauthorized() {
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                postService.approvePost("user", "Test Author", 1L));

        assertEquals("Admin role required", exception.getMessage());
    }

    @Test
    public void testUpdatePostUnauthorized() {
        PostRequest request = new PostRequest("Updated Title", "Updated Content");

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                postService.updatePost("user", "Test Author", 1L, request));

        assertEquals("Admin role required", exception.getMessage());
    }

    @Test
    public void testUpdatePostSuccess() {
        PostRequest request = new PostRequest("Updated Title", "Updated Content");
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        postService.updatePost("admin", "Test Author", 1L, request);

        assertEquals("Updated Title", testPost.getTitle());
        assertEquals("Updated Content", testPost.getContent());
        verify(postRepository, times(1)).save(testPost);
    }

    @Test
    public void testUpdatePostDifferentAuthor() {
        PostRequest request = new PostRequest("Updated Title", "Updated Content");
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                postService.updatePost("admin", "Different Author", 1L, request));

        assertEquals("You must be logged in as the author", exception.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    public void testFindAcceptedPosts() {
        testPost.setAccepted(true);
        when(postRepository.findPostsByAccepted(true)).thenReturn(Collections.singletonList(testPost));

        List<PostResponse> acceptedPosts = postService.findAccepted();

        assertEquals(1, acceptedPosts.size());
        assertEquals("Test Title", acceptedPosts.get(0).getTitle());
        assertTrue(acceptedPosts.get(0).isAccepted());
    }

    @Test
    public void testApprovePostSuccess() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        postService.approvePost("admin", "Another Admin", 1L);

        assertTrue(testPost.isAccepted());
        verify(postRepository, times(1)).save(testPost);
    }

    @Test
    public void testApproveOwnPostUnauthorized() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                postService.approvePost("admin", "Test Author", 1L));

        assertEquals("You cannot approve your own post", exception.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    public void testGetEmailByPostIdSuccess() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        String email = postService.getEmailByPostId(1L);

        assertEquals("author@test.com", email);
    }

    @Test
    public void testGetEmailByPostIdNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                postService.getEmailByPostId(1L));

        assertEquals("Post with ID 1 not found", exception.getMessage());
    }

    @Test
    public void testFindPostByIdNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                postService.findPostById(1L));

        assertEquals("Post with ID 1 not found", exception.getMessage());
    }

    @Test
    public void testApprovePostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                postService.approvePost("admin", "Different Author", 1L));

        assertEquals("Post with ID 1 not found", exception.getMessage());
        verify(postRepository, never()).save(any());
    }
}