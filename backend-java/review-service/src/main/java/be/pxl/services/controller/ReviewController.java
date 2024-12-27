package be.pxl.services.controller;

import be.pxl.services.controller.dto.ReviewRequest;
import be.pxl.services.controller.dto.ReviewResponse;
import be.pxl.services.services.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final IReviewService reviewService;

    @GetMapping(path = "{postId}")
    public ResponseEntity<List<ReviewResponse>> getReviews(@RequestHeader("X-Role") String role, @PathVariable long postId) {
        List<ReviewResponse> reviews = reviewService.getReviews(role, postId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @PostMapping(path = "approve/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void approveReview(@RequestHeader("X-Role") String role, @RequestHeader("X-Name") String name, @PathVariable long postId) {
        reviewService.approveReview(role, name, postId);
    }

    @PostMapping(path = "decline/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void declineReview(@RequestHeader("X-Role") String role, @PathVariable long postId, @RequestBody ReviewRequest request) {
        reviewService.declineReview(role, postId, request);
    }
}
