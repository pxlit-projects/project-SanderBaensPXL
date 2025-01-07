import {Component, inject, ViewChild} from '@angular/core';
import {FormsModule, NgForm} from "@angular/forms";
import {PostRequest} from "../../../shared/model/post-request.model";
import {PostService} from "../../../shared/services/post.service";
import {CanComponentDeactivate} from "../../../shared/guards/unsaved-changes.guard";

@Component({
  selector: 'app-add-post',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './add-post.component.html',
  styleUrl: './add-post.component.css'
})
export class AddPostComponent implements CanComponentDeactivate{
  @ViewChild('myForm') myForm!: NgForm;
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

  hasUnsavedChanges(): boolean | null{
    return this.myForm.dirty;
  }
}
