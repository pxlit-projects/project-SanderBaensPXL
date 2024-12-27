package be.pxl.services.repository;

import be.pxl.services.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findPostsByAcceptedAndAuthor(boolean accepted, String author);
    List<Post> findPostsByAccepted(boolean accepted);
}
