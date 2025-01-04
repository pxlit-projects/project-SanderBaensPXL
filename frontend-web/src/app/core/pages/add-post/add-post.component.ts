import {Component, inject} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {PostRequest} from "../../../shared/model/post-request.model";
import {PostService} from "../../../shared/services/post.service";

@Component({
  selector: 'app-add-post',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './add-post.component.html',
  styleUrl: './add-post.component.css'
})
export class AddPostComponent {
  postService: PostService = inject(PostService);
  request: PostRequest = new PostRequest("", "");

  submitSuccess: boolean = false;
  submitError: boolean = false;

  onSubmit(form: any): void {
    if(form.valid) {
      this.submitSuccess = false;
      this.submitError = false;
      this.postService.addPost(this.request).subscribe({
        next: (response) => {
          console.log('Post added successfully', response);
          this.submitSuccess = true;
          this.request = new PostRequest("", "");
          form.resetForm();
        },
        error: (error) => {
          console.error('Error adding post', error);
          this.submitError = true;
        }
      });
    }
  }
}
