package be.pxl.services.controller;

import be.pxl.services.controller.dto.CommentRequest;
import be.pxl.services.controller.dto.CommentResponse;
import be.pxl.services.services.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final ICommentService commentService;

    @PostMapping(path = "{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addComment(@RequestHeader("X-Name") String name, @PathVariable long postId, @RequestBody CommentRequest commentRequest) {
        commentService.addComment(name, postId, commentRequest);
    }

    @GetMapping(path = "{postId}")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable long postId) {
        List<CommentResponse> comments = commentService.getComments(postId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PutMapping(path="{postId}/{commentId}")
    public void updateComment(@RequestHeader("X-Name") String name, @PathVariable long postId, @PathVariable long commentId, @RequestBody CommentRequest commentRequest) {
        commentService.updateComment(name, postId, commentId, commentRequest);
    }

    @DeleteMapping(path="{postId}/{commentId}")
    public void deleteComment(@RequestHeader("X-Name") String name, @PathVariable long postId, @PathVariable long commentId) {
        commentService.deleteComment(name, postId, commentId);
    }
}
