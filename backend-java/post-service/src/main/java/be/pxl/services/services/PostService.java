package be.pxl.services.services;

import be.pxl.services.controller.dto.PostRequest;
import be.pxl.services.controller.dto.PostResponse;
import be.pxl.services.domain.Post;
import be.pxl.services.exception.UnauthorizedException;
import be.pxl.services.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final PostRepository postRepository;

    private PostResponse mapToPostResponse(Post post) {
        return PostResponse.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .createdDate(post.getCreatedDate())
                .build();
    }

    @Override
    public void addPost(String role, String name, PostRequest postRequest) {
        if (!Objects.equals(role, "admin")) {
            throw new UnauthorizedException("Admin role required");
        }

        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .author(name)
                .createdDate(LocalDateTime.now())
                .accepted(false)
                .build();
        postRepository.save(post);
    }

    @Override
    public void updatePost(String role, String name, long id, PostRequest postRequest) {
        if (!Objects.equals(role, "admin")) {
            throw new UnauthorizedException("Admin role required");
        }

        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post with ID " + id + " not found"));

        if (!Objects.equals(name, existingPost.getAuthor())) {
            throw new UnauthorizedException("You must be logged in as the author");
        }

        existingPost.setTitle(postRequest.getTitle());
        existingPost.setContent(postRequest.getContent());
        postRepository.save(existingPost);
    }

    @Override
    public List<PostResponse> findUnaccepted(String role, String name) {
        if (!Objects.equals(role, "admin")) {
            throw new UnauthorizedException("Admin role required");
        }

        List<Post> unacceptedPosts = postRepository.findPostsByAcceptedAndAuthor(false, name);
        return unacceptedPosts.stream().map(this::mapToPostResponse).toList();
    }

    @Override
    public List<PostResponse> findAccepted() {
        List<Post> acceptedPosts = postRepository.findPostByAccepted(true);
        return acceptedPosts.stream().map(this::mapToPostResponse).toList();
    }
}
