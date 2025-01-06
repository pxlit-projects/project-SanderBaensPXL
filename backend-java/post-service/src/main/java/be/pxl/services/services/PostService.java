package be.pxl.services.services;

import be.pxl.services.controller.dto.PostRequest;
import be.pxl.services.controller.dto.PostResponse;
import be.pxl.services.domain.Post;
import be.pxl.services.exception.UnauthorizedException;
import be.pxl.services.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {

    private static final Logger log = LoggerFactory.getLogger(PostService.class);
    private final PostRepository postRepository;

    private PostResponse mapToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .createdDate(post.getCreatedDate())
                .accepted(post.isAccepted())
                .build();
    }

    @Override
    public void addPost(String role, String name, String email, PostRequest postRequest) {
        if (!Objects.equals(role, "admin")) {
            log.warn("Admin role required for adding post");
            throw new UnauthorizedException("Admin role required");
        }

        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .author(name)
                .email(email)
                .createdDate(LocalDateTime.now())
                .accepted(false)
                .build();
        postRepository.save(post);
    }

    @Override
    public void updatePost(String role, String name, long id, PostRequest postRequest) {
        if (!Objects.equals(role, "admin")) {
            log.warn("Admin role required for updating post");
            throw new UnauthorizedException("Admin role required");
        }

        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post with ID " + id + " not found"));

        if (!Objects.equals(name, existingPost.getAuthor())) {
            log.warn("User {} tried to change a post of user {}", name, existingPost.getAuthor());
            throw new UnauthorizedException("You must be logged in as the author");
        }

        existingPost.setTitle(postRequest.getTitle());
        existingPost.setContent(postRequest.getContent());
        postRepository.save(existingPost);
    }

    @Override
    public List<PostResponse> findUnaccepted(String role) {
        if (!Objects.equals(role, "admin")) {
            log.warn("Admin role required for finding unaccepted posts");
            throw new UnauthorizedException("Admin role required");
        }

        List<Post> unacceptedPosts = postRepository.findPostsByAccepted(false);
        return unacceptedPosts.stream().map(this::mapToPostResponse).toList();
    }

    @Override
    public List<PostResponse> findAccepted() {
        List<Post> acceptedPosts = postRepository.findPostsByAccepted(true);
        return acceptedPosts.stream().map(this::mapToPostResponse).toList();
    }

    @Override
    public void approvePost(String role, String name, long id) {
        if (!Objects.equals(role, "admin")) {
            log.warn("Admin role required for approving post");
            throw new UnauthorizedException("Admin role required");
        }

        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post with ID " + id + " not found"));

        if (Objects.equals(name, existingPost.getAuthor())) {
            log.warn("User {} tried to approve his own post", name);
            throw new UnauthorizedException("You cannot approve your own post");
        }

        existingPost.setAccepted(true);
        postRepository.save(existingPost);
    }

    @Override
    public PostResponse findPostById(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post with ID " + id + " not found"));
        return mapToPostResponse(post);
    }

    @Override
    public String getEmailByPostId(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post with ID " + id + " not found"));
        return post.getEmail();
    }


}
