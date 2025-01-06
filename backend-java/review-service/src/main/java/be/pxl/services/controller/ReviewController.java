package be.pxl.services.controller;

import be.pxl.services.controller.dto.ReviewRequest;
import be.pxl.services.controller.dto.ReviewResponse;
import be.pxl.services.services.IReviewService;
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
public class ReviewController {

    private final IReviewService reviewService;
    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);

    @GetMapping(path = "{postId}")
    public ResponseEntity<List<ReviewResponse>> getReviews(@RequestHeader("X-Role") String role, @PathVariable long postId) {
        log.info("Fetching reviews");
        List<ReviewResponse> reviews = reviewService.getReviews(role, postId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @PostMapping(path = "approve/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void approveReview(@RequestHeader("X-Role") String role, @RequestHeader("X-Name") String name, @PathVariable long postId) {
        log.info("Approving review");
        reviewService.approveReview(role, name, postId);
    }

    @PostMapping(path = "decline/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void declineReview(@RequestHeader("X-Role") String role, @RequestHeader("X-Name") String name, @PathVariable long postId, @RequestBody ReviewRequest request) {
        log.info("Declining review");
        reviewService.declineReview(role, name, postId, request);
    }
}
