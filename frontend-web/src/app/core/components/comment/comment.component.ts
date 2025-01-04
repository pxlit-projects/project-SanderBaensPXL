import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommentService } from '../../../shared/services/comment.service';
import { Comment } from '../../../shared/model/comment.model';
import { AuthService } from '../../../shared/services/auth.service';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-comment',
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.css']
})
export class CommentComponent implements OnInit {
  postId: number = 0;
  comments: Comment[] = [];
  newComment: string = '';
  editingComment: Comment | null = null;
  error: string | null = null;

  authService: AuthService = inject(AuthService);
  route: ActivatedRoute = inject(ActivatedRoute);
  commentService: CommentService = inject(CommentService);

  ngOnInit() {
    this.postId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.postId) {
      this.loadComments();
    } else {
      this.error = 'Invalid post ID';
    }
  }

  loadComments(): void {
    this.commentService.getComments(this.postId).subscribe({
      next: (response) => {
        this.comments = response;
      },
      error: (error) => {
        console.error('Error fetching comments:', error);
        this.error = 'Failed to load comments. Please try again later.';
      }
    });
  }

  addComment(): void {
    if (!this.newComment.trim()) {
      this.error = 'Comment cannot be empty';
      return;
    }
    this.commentService.addComment(this.postId, this.newComment).subscribe({
      next: () => {
        this.loadComments();
        this.newComment = '';
      },
      error: (error) => {
        console.error('Error adding comment:', error);
        this.error = 'Failed to add comment. Please try again later.';
      }
    });
  }

  startEditing(comment: Comment): void {
    this.editingComment = { ...comment };
  }

  updateComment(): void {
    if (this.editingComment) {
      this.commentService.updateComment(this.postId, this.editingComment.id, this.editingComment.comment).subscribe({
        next: () => {
          this.loadComments();
          this.editingComment = null;
        },
        error: (error) => {
          console.error('Error updating comment:', error);
          this.error = 'Failed to update comment. Please try again later.';
        }
      });
    }
  }

  deleteComment(commentId: number): void {
    this.commentService.deleteComment(this.postId, commentId).subscribe({
      next: () => {
        this.loadComments();
      },
      error: (error) => {
        console.error('Error deleting comment:', error);
        this.error = 'Failed to delete comment. Please try again later.';
      }
    });
  }
}
