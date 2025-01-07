import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import {provideHttpClient} from "@angular/common/http";
import { PostService } from './post.service';
import { AuthService } from './auth.service';
import { PostRequest } from '../model/post-request.model';
import { Post } from '../model/post.model';

describe('PostService', () => {
  let service: PostService;
  let httpMock: HttpTestingController;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  const mockPosts: Post[] = [
    new Post(1, 'Post 1', 'Content for post 1', 'Author 1', new Date('2023-01-01'), true),
    new Post(2, 'Post 2', 'Content for post 2', 'Author 2', new Date('2023-01-02'), false),
  ];

  beforeEach(() => {
    const authSpy = jasmine.createSpyObj('AuthService', ['getHeaders']);

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        PostService,
        { provide: AuthService, useValue: authSpy },
      ],
    });

    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;

    authServiceSpy.getHeaders.and.returnValue({ 'X-Role': 'admin' } as any);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch posts', () => {
    const accepted = 'true';

    service.getPosts(accepted).subscribe((posts) => {
      expect(posts).toEqual(mockPosts);
    });

    const req = httpMock.expectOne(service.BASE_URL + '/' + accepted);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.has('X-Role')).toBeTrue();
    req.flush(mockPosts);
  });

  it('should fetch a post by ID', () => {
    const postId = 1;

    service.getPostById(postId).subscribe((post) => {
      expect(post).toEqual(mockPosts[0]);
    });

    const req = httpMock.expectOne(service.BASE_URL + '/' + postId);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.has('X-Role')).toBeTrue();
    req.flush(mockPosts[0]);
  });

  it('should add a new post', () => {
    const postRequest: PostRequest = { title: 'New Post', content: 'New post content' };
    const response = { message: 'Post added successfully' };

    service.addPost(postRequest).subscribe((res) => {
      expect(res).toEqual(response);
    });

    const req = httpMock.expectOne(service.BASE_URL);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(postRequest);
    expect(req.request.headers.has('X-Role')).toBeTrue();
    req.flush(response);
  });

  it('should update an existing post', () => {
    const postId = 1;
    const postRequest: PostRequest = { title: 'Updated Post', content: 'Updated post content' };
    const response = { message: 'Post updated successfully' };

    service.updatePost(postId, postRequest).subscribe((res) => {
      expect(res).toEqual(response);
    });

    const req = httpMock.expectOne(service.BASE_URL + '/' + postId);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(postRequest);
    expect(req.request.headers.has('X-Role')).toBeTrue();
    req.flush(response);
  });

  it('should handle errors gracefully', () => {
    const errorMessage = 'Something bad happened; please try again later.';
    const mockError = { status: 500, statusText: 'Internal Server Error' };

    service.getPosts('true').subscribe({
      next: () => fail('Expected error, but got success response'),
      error: (error) => {
        expect(error.message).toBe(errorMessage);
      },
    });

    const req = httpMock.expectOne(service.BASE_URL + '/true');
    req.flush('Error', mockError);
  });
});
