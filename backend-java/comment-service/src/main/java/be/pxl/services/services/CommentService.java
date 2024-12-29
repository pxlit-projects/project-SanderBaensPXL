package be.pxl.services.services;

import be.pxl.services.controller.dto.CommentRequest;
import be.pxl.services.controller.dto.CommentResponse;
import be.pxl.services.domain.Comment;
import be.pxl.services.exception.UnauthorizedException;
import be.pxl.services.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;

    private CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .author(comment.getAuthor())
                .comment(comment.getComment())
                .createdDate(comment.getCreatedDate())
                .build();
    }

    @Override
    public void addComment(String name, long postId, CommentRequest commentRequest) {
        Comment comment = Comment.builder()
                .author(name)
                .postId(postId)
                .comment(commentRequest.getComment())
                .createdDate(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
    }

    @Override
    public List<CommentResponse> getComments(long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream().map(this::mapToCommentResponse).toList();
    }

    @Override
    public void updateComment(String name, long postId, long commentId, CommentRequest commentRequest) {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment with ID " + postId + " not found"));

        if (Objects.equals(name, existingComment.getAuthor())) {
            throw new UnauthorizedException("You can only change your own comments");
        }
        existingComment.setComment(commentRequest.getComment());
        commentRepository.save(existingComment);
    }

    @Override
    public void deleteComment(String name, long postId, long commentId) {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment with ID " + postId + " not found"));

        if (Objects.equals(name, existingComment.getAuthor())) {
            throw new UnauthorizedException("You can only delete your own comments");
        }

        commentRepository.delete(existingComment);
    }
}
