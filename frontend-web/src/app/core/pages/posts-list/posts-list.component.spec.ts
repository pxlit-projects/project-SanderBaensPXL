import { TestBed, ComponentFixture } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

import { PostsListComponent } from './posts-list.component';
import { PostService } from '../../../shared/services/post.service';
import { Post } from '../../../shared/model/post.model';

describe('PostsListComponent', () => {
  let fixture: ComponentFixture<PostsListComponent>;
  let component: PostsListComponent;

  let mockPostService: jasmine.SpyObj<PostService>;
  let mockActivatedRoute: jasmine.SpyObj<ActivatedRoute>;

  beforeEach(async () => {
    mockPostService = jasmine.createSpyObj('PostService', ['getPosts']);
    mockActivatedRoute = {
      paramMap: of({ get: () => '' })
    } as any;

    await TestBed.configureTestingModule({
      imports: [
        FormsModule,
        ReactiveFormsModule,
        PostsListComponent
      ],
      providers: [
        { provide: PostService, useValue: mockPostService },
        { provide: DatePipe, useClass: DatePipe },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PostsListComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should call getPosts on ngOnInit', () => {
    mockPostService.getPosts.and.returnValue(of([]));
    fixture.detectChanges();
    expect(mockPostService.getPosts).toHaveBeenCalled();
  });

  it('should handle empty posts array', () => {
    mockPostService.getPosts.and.returnValue(of([]));
    fixture.detectChanges();

    expect(component.posts).toEqual([]);
    expect(component.filteredPosts).toEqual([]);
  });

  it('should filter posts by title', () => {
    const mockPosts: Post[] = [
      { id: 1, title: 'Test Title', content: 'Test content', author: 'John Doe', createdDate: new Date('2024-01-01'), accepted: true },
      { id: 2, title: 'Another Test', content: 'More test content', author: 'Jane Smith', createdDate: new Date('2024-01-02'), accepted: false }
    ];
    mockPostService.getPosts.and.returnValue(of(mockPosts));

    fixture.detectChanges();

    component.filterForm.setValue({ title: 'test', author: '', date: null, content: '' });

    expect(component.filteredPosts.length).toBe(2);
  });

  it('should filter posts by date', () => {
    const mockPosts: Post[] = [
      { id: 1, title: 'Test Title', content: 'Test content', author: 'John Doe', createdDate: new Date('2024-01-01'), accepted: true },
      { id: 2, title: 'Another Test', content: 'More test content', author: 'Jane Smith', createdDate: new Date('2024-01-02'), accepted: false }
    ];
    mockPostService.getPosts.and.returnValue(of(mockPosts));

    fixture.detectChanges();

    component.filterForm.setValue({ title: '', author: '', date: '2024-01-01', content: '' });

    expect(component.filteredPosts.length).toBe(1);
    expect(component.filteredPosts[0].id).toBe(1);
  });

  it('should filter posts by multiple criteria', () => {
    const mockPosts: Post[] = [
      { id: 1, title: 'Test Title', content: 'Test content', author: 'John Doe', createdDate: new Date('2024-01-01'), accepted: true },
      { id: 2, title: 'Another Test', content: 'More test content', author: 'Jane Smith', createdDate: new Date('2024-01-02'), accepted: false }
    ];
    mockPostService.getPosts.and.returnValue(of(mockPosts));

    fixture.detectChanges();

    component.filterForm.setValue({ title: 'test', author: 'John', date: '2024-01-01', content: '' });

    expect(component.filteredPosts.length).toBe(1);
    expect(component.filteredPosts[0].id).toBe(1);
  });

  it('should handle error when fetching posts', () => {
    mockPostService.getPosts.and.returnValue(throwError(() => new Error('Failed to fetch posts')));

    spyOn(console, 'error');

    fixture.detectChanges();

    expect(console.error).toHaveBeenCalledWith('Error fetching posts:', jasmine.any(Error));
  });
});
