package be.pxl.services.controller;

import be.pxl.services.controller.dto.CommentRequest;
import be.pxl.services.controller.dto.CommentResponse;
import be.pxl.services.services.ICommentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final ICommentService commentService;
    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    @PostMapping(path = "{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addComment(@RequestHeader("X-Name") String name, @PathVariable long postId, @RequestBody CommentRequest commentRequest) {
        log.info("Adding a comment");
        commentService.addComment(name, postId, commentRequest);
    }

    @GetMapping(path = "{postId}")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable long postId) {
        log.info("Fetching all comments for post {}", postId);
        List<CommentResponse> comments = commentService.getComments(postId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PutMapping(path="{postId}/{commentId}")
    public void updateComment(@RequestHeader("X-Name") String name, @PathVariable long postId, @PathVariable long commentId, @RequestBody CommentRequest commentRequest) {
        log.info("Updating comment with id {}", commentId);
        commentService.updateComment(name, postId, commentId, commentRequest);
    }

    @DeleteMapping(path="{postId}/{commentId}")
    public void deleteComment(@RequestHeader("X-Name") String name, @PathVariable long postId, @PathVariable long commentId) {
        log.info("Deleting comment with id {}", commentId);
        commentService.deleteComment(name, postId, commentId);
    }
}
