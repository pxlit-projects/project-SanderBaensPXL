package be.pxl.services.services;

import be.pxl.services.client.PostClient;
import be.pxl.services.controller.dto.ReviewRequest;
import be.pxl.services.controller.dto.ReviewResponse;
import be.pxl.services.domain.Review;
import be.pxl.services.exception.UnauthorizedException;
import be.pxl.services.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {

    private final ReviewRepository reviewRepository;
    private final PostClient postClient;
    private final ReviewMessageSender messageSender;
    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private ReviewResponse mapToPostResponse(Review review) {
        return ReviewResponse.builder()
                .author(review.getAuthor())
                .comment(review.getComment())
                .build();
    }

    @Override
    public List<ReviewResponse> getReviews(String role, long postId) {
        if (!Objects.equals(role, "admin")) {
            log.warn("Admin role required for fetching reviews");
            throw new UnauthorizedException("Admin role required");
        }
        List<Review> reviews = reviewRepository.findByPostId(postId);
        return reviews.stream().map(this::mapToPostResponse).toList();
    }

    @Override
    @Transactional
    public void approveReview(String role, String name, long postId) {
        if (!Objects.equals(role, "admin")) {
            log.warn("Admin role required for approving reviews");
            throw new UnauthorizedException("Admin role required");
        }
        postClient.approvePost(postId, role, name);

        String email = postClient.getEmailByPostId(postId);
        messageSender.sendReviewMessage(String.valueOf(postId), "approved", email);

        reviewRepository.deleteReviewsByPostId(postId);
    }

    @Override
    public void declineReview(String role, String name, long postId, ReviewRequest request) {
        if (!Objects.equals(role, "admin")) {
            log.warn("Admin role required for declining reviews");
            throw new UnauthorizedException("Admin role required");
        }
        Review review = Review.builder()
                .author(name)
                .comment(request.getComment())
                .postId(postId)
                .build();
        reviewRepository.save(review);

        String email = postClient.getEmailByPostId(postId);
        messageSender.sendReviewMessage(String.valueOf(postId), "declined", email);
    }
}
