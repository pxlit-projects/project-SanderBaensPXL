package be.pxl.services.controller;

import be.pxl.services.controller.dto.PostRequest;
import be.pxl.services.controller.dto.PostResponse;
import be.pxl.services.services.IPostService;
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
public class PostController {

    private static final Logger log = LoggerFactory.getLogger(PostController.class);
    private final IPostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addPost(@RequestHeader("X-Role") String role, @RequestHeader("X-Name") String name, @RequestHeader("X-Email") String email, @RequestBody PostRequest postRequest) {
        log.info("Adding new post");
        postService.addPost(role, name, email, postRequest);
    }

    @PutMapping(path = "{id}")
    public void updatePost(@RequestHeader("X-Role") String role, @RequestHeader("X-Name") String name, @PathVariable long id, @RequestBody PostRequest postRequest) {
        log.info("Updating post");
        postService.updatePost(role, name, id, postRequest);
    }

    @GetMapping(path = "unaccepted")
    public ResponseEntity<List<PostResponse>> getUnacceptedPost(@RequestHeader("X-Role") String role) {
        log.info("Fetching unaccepted post");
        List<PostResponse> unacceptedPosts = postService.findUnaccepted(role);
        return new ResponseEntity<>(unacceptedPosts, HttpStatus.OK);
    }

    @GetMapping(path = "accepted")
    public ResponseEntity<List<PostResponse>> getAcceptedPost() {
        log.info("Fetching accepted post");
        List<PostResponse> unacceptedPosts = postService.findAccepted();
        return new ResponseEntity<>(unacceptedPosts, HttpStatus.OK);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable long id){
        log.info("Fetching post by id");
        PostResponse response = postService.findPostById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(path = "approve/{id}")
    public void approvePost(@RequestHeader("X-Role") String role, @RequestHeader("X-Name") String name, @PathVariable long id) {
        log.info("Approving post");
        postService.approvePost(role, name, id);
    }

    @GetMapping(path = "{id}/email")
    public ResponseEntity<String> getEmailByPostId(@PathVariable long id) {
        log.info("Fetching email by id");
        String email = postService.getEmailByPostId(id);
        return new ResponseEntity<>(email, HttpStatus.OK);
    }
}