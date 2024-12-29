package be.pxl.services.services;

import be.pxl.services.controller.dto.CommentRequest;
import be.pxl.services.controller.dto.CommentResponse;

import java.util.List;

public interface ICommentService {
    void addComment(String name, long postId, CommentRequest commentRequest);

    List<CommentResponse> getComments(long postId);

    void updateComment(String name, long postId, long commentId, CommentRequest commentRequest);

    void deleteComment(String name, long postId, long commentId);
}
