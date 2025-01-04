import {Component, inject, Input, OnInit} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {ReviewService} from "../../../shared/services/review.service";
import {Review} from "../../../shared/model/review.model";
import {NgClass} from "@angular/common";
import { AuthService } from '../../../shared/services/auth.service';
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-review',
  standalone: true,
  imports: [
    NgClass,
    FormsModule
  ],
  templateUrl: './review.component.html',
  styleUrl: './review.component.css'
})
export class ReviewComponent implements OnInit {
  @Input() author: string = '';
  isApproved: boolean = false;
  declineComment: string = '';
  postId: number = 0;

  authService: AuthService = inject(AuthService);
  route: ActivatedRoute = inject(ActivatedRoute);
  reviewService: ReviewService = inject(ReviewService);
  reviews: Review[] = [];
  error: string | null = null;

  ngOnInit() {
    this.postId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.postId) {
      this.loadReviews(this.postId);
    } else {
      this.error = 'Invalid post ID';
    }
  }

  loadReviews(postId: number): void {
    this.reviewService.getReviews(postId).subscribe({
      next: (response) => {
        this.reviews = response;
      },
      error: (error) => {
        console.error('Error fetching reviews:', error);
        this.error = 'Failed to load reviews. Please try again later.';
      }
    });
  }

  submitReview(): void {
    if (this.isApproved) {
      this.reviewService.approve(this.postId).subscribe({
        next: () => {
          console.log('Post approved successfully');
          this.loadReviews(this.postId);
        },
        error: (error) => {
          console.error('Error approving post:', error);
          this.error = 'Failed to approve post. Please try again later.';
        }
      });
    } else {
      if (!this.declineComment) {
        this.error = 'Please provide a reason for declining the post.';
        return;
      }
      this.reviewService.decline(this.postId, this.declineComment).subscribe({
        next: () => {
          console.log('Post declined successfully');
          this.loadReviews(this.postId);
          this.declineComment = '';
        },
        error: (error) => {
          console.error('Error declining post:', error);
          this.error = 'Failed to decline post. Please try again later.';
        }
      });
    }
  }
}
