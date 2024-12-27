package be.pxl.services.services;

import be.pxl.services.controller.dto.ReviewRequest;
import be.pxl.services.controller.dto.ReviewResponse;

import java.util.List;

public interface IReviewService {
    List<ReviewResponse> getReviews(String role, long postId);

    void approveReview(String role, String name, long postId);

    void declineReview(String role, long postId, ReviewRequest request);
}
