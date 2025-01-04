import {Component, inject, OnInit} from '@angular/core';
import { ActivatedRoute } from "@angular/router";
import { PostService } from "../../../shared/services/post.service";
import { Post } from "../../../shared/model/post.model";
import { DatePipe } from "@angular/common";
import { PostRequest } from "../../../shared/model/post-request.model";
import { FormsModule } from "@angular/forms";
import {AuthService} from "../../../shared/services/auth.service";
import {ReviewComponent} from "../../components/review/review.component";
import {CommentComponent} from "../../components/comment/comment.component";

@Component({
  selector: 'app-post',
  standalone: true,
  imports: [
    DatePipe,
    FormsModule,
    ReviewComponent,
    CommentComponent
  ],
  templateUrl: './post.component.html',
  styleUrl: './post.component.css'
})
export class PostComponent implements OnInit {
  post: Post | null = null;
  error: string | null = null;
  request: PostRequest = new PostRequest("", "");
  id: number = 0;
  isEditing: boolean = false;
  submitSuccess: boolean = false;
  submitError: boolean = false;

  postService: PostService = inject(PostService);
  route: ActivatedRoute = inject(ActivatedRoute);
  authService: AuthService = inject(AuthService);

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    if (this.id !== 0) {
      this.loadPost();
    } else {
      this.error = 'Invalid post ID';
    }
  }

  loadPost(): void {
    this.postService.getPostById(this.id).subscribe({
      next: (post) => {
        this.post = post;
        this.resetRequest();
      },
      error: (error) => {
        console.error('Error fetching post:', error);
        this.error = 'Failed to load the post. Please try again later.';
      }
    });
  }

  resetRequest(): void {
    if (this.post) {
      this.request = new PostRequest(this.post.title, this.post.content);
    }
  }

  startEditing(): void {
    this.isEditing = true;
    this.resetRequest();
  }

  cancelEditing(): void {
    this.isEditing = false;
  }

  onSubmit(form: any): void {
    if(form.valid) {
      this.submitSuccess = false;
      this.submitError = false;
      this.postService.updatePost(this.id, this.request).subscribe({
        next: (response) => {
          console.log('Post updated successfully', response);
          this.submitSuccess = true;
          this.isEditing = false;
          this.loadPost();
        },
        error: (error) => {
          console.error('Error updating post', error);
          this.submitError = true;
        }
      });
    } else {
      this.submitError = true;
    }
  }
}
