package be.pxl.services.controller;

import be.pxl.services.controller.dto.PostRequest;
import be.pxl.services.controller.dto.PostResponse;
import be.pxl.services.services.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final IPostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addPost(@RequestHeader("X-Role") String role, @RequestHeader("X-Name") String name, @RequestBody PostRequest postRequest) {postService.addPost(role, name, postRequest);}

    @PutMapping(path = "{id}")
    public void updatePost(@RequestHeader("X-Role") String role, @RequestHeader("X-Name") String name, @PathVariable long id, @RequestBody PostRequest postRequest) {postService.updatePost(role, name, id, postRequest);}

    @GetMapping(path = "unaccepted")
    public ResponseEntity<List<PostResponse>> getUnacceptedPost(@RequestHeader("X-Role") String role) {
        List<PostResponse> unacceptedPosts = postService.findUnaccepted(role);
        return new ResponseEntity<>(unacceptedPosts, HttpStatus.OK);
    }

    @GetMapping(path = "accepted")
    public ResponseEntity<List<PostResponse>> getAcceptedPost() {
        List<PostResponse> unacceptedPosts = postService.findAccepted();
        return new ResponseEntity<>(unacceptedPosts, HttpStatus.OK);
    }

    @PutMapping(path = "approve/{id}")
    public void approvePost(@RequestHeader("X-Role") String role, @RequestHeader("X-Name") String name, @PathVariable long id) {postService.approvePost(role, name, id);}

}