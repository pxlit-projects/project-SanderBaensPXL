import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { of } from 'rxjs';

import { AddPostComponent } from './add-post.component';
import { PostService } from '../../../shared/services/post.service';

describe('AddPostComponent', () => {
  let component: AddPostComponent;
  let fixture: ComponentFixture<AddPostComponent>;
  let mockPostService: jasmine.SpyObj<PostService>;

  beforeEach(async () => {
    mockPostService = jasmine.createSpyObj('PostService', ['addPost']);
    mockPostService.addPost.and.returnValue(of({}));

    await TestBed.configureTestingModule({
      imports: [FormsModule, AddPostComponent],
      providers: [
        { provide: PostService, useValue: mockPostService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AddPostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });
});
