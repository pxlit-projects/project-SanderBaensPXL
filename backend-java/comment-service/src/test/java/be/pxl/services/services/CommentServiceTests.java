package be.pxl.services.services;

import be.pxl.services.controller.dto.CommentRequest;
import be.pxl.services.controller.dto.CommentResponse;
import be.pxl.services.domain.Comment;
import be.pxl.services.exception.UnauthorizedException;
import be.pxl.services.repository.CommentRepository;
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
public class CommentServiceTests {

    @Container
    private static final MySQLContainer<?> sqlContainer = new MySQLContainer<>("mysql:5.7.37");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @MockBean
    private CommentRepository commentRepository;

    private CommentService commentService;

    private Comment testComment;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(commentRepository);

        testComment = Comment.builder()
                .id(1L)
                .author("Test Author")
                .postId(100L)
                .comment("Test Comment")
                .createdDate(LocalDateTime.now())
                .build();
    }

    @Test
    public void testAddCommentSuccess() {
        CommentRequest request = new CommentRequest("New Comment");

        commentService.addComment("Test Author", 100L, request);

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());

        Comment savedComment = captor.getValue();
        assertEquals("Test Author", savedComment.getAuthor());
        assertEquals(100L, savedComment.getPostId());
        assertEquals("New Comment", savedComment.getComment());
    }

    @Test
    public void testGetCommentsSuccess() {
        when(commentRepository.findByPostId(100L)).thenReturn(Collections.singletonList(testComment));

        List<CommentResponse> responses = commentService.getComments(100L);

        assertEquals(1, responses.size());
        assertEquals("Test Comment", responses.get(0).getComment());
        assertEquals("Test Author", responses.get(0).getAuthor());
    }

    @Test
    public void testUpdateCommentSuccess() {
        CommentRequest request = new CommentRequest("Updated Comment");
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        commentService.updateComment("Test Author", 100L, 1L, request);

        assertEquals("Updated Comment", testComment.getComment());
        verify(commentRepository).save(testComment);
    }

    @Test
    public void testUpdateCommentUnauthorized() {
        CommentRequest request = new CommentRequest("Updated Comment");
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                commentService.updateComment("Different Author", 100L, 1L, request));

        assertEquals("You can only change your own comments", exception.getMessage());
        verify(commentRepository, never()).save(any());
    }

    @Test
    public void testDeleteCommentSuccess() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        commentService.deleteComment("Test Author", 100L, 1L);

        verify(commentRepository).delete(testComment);
    }

    @Test
    public void testDeleteCommentUnauthorized() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                commentService.deleteComment("Different Author", 100L, 1L));

        assertEquals("You can only delete your own comments", exception.getMessage());
        verify(commentRepository, never()).delete(any());
    }

    @Test
    public void testDeleteCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                commentService.deleteComment("Test Author", 100L, 1L));

        assertEquals("Comment with ID 1 not found", exception.getMessage());
        verify(commentRepository, never()).delete(any());
    }

    @Test
    public void testUpdateCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        CommentRequest request = new CommentRequest("Updated Comment");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                commentService.updateComment("Test Author", 100L, 1L, request));

        assertEquals("Comment with ID 1 not found", exception.getMessage());
        verify(commentRepository, never()).save(any());
    }
}
