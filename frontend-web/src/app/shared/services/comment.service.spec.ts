import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { CommentService } from './comment.service';
import { AuthService } from './auth.service';
import { Comment } from '../model/comment.model';
import {provideHttpClient} from "@angular/common/http";

describe('CommentService', () => {
  let service: CommentService;
  let httpMock: HttpTestingController;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  const mockComments: Comment[] = [
    new Comment(1, 'John Doe', 'Test comment 1', new Date('2023-01-01')),
    new Comment(2, 'Jane Doe', 'Test comment 2', new Date('2023-01-02')),
  ];

  beforeEach(() => {
    const authSpy = jasmine.createSpyObj('AuthService', ['getHeaders']);

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        CommentService,
        { provide: AuthService, useValue: authSpy },
      ],
    });

    service = TestBed.inject(CommentService);
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

  it('should fetch comments for a post', () => {
    const postId = 1;

    service.getComments(postId).subscribe((comments) => {
      expect(comments).toEqual(mockComments);
    });

    const req = httpMock.expectOne(service.BASE_URL + '/' + postId);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.has('X-Role')).toBeTrue();
    req.flush(mockComments);
  });

  it('should add a comment to a post', () => {
    const postId = 1;
    const newComment = 'New comment';
    const response = { message: 'Comment added successfully' };

    service.addComment(postId, newComment).subscribe((res) => {
      expect(res).toEqual(response);
    });

    const req = httpMock.expectOne(service.BASE_URL + '/' + postId);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ comment: newComment });
    expect(req.request.headers.has('X-Role')).toBeTrue();
    req.flush(response);
  });

  it('should update a comment', () => {
    const postId = 1;
    const commentId = 2;
    const updatedComment = 'Updated comment';
    const response = { message: 'Comment updated successfully' };

    service.updateComment(postId, commentId, updatedComment).subscribe((res) => {
      expect(res).toEqual(response);
    });

    const req = httpMock.expectOne(service.BASE_URL + `/${postId}/${commentId}`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ comment: updatedComment });
    expect(req.request.headers.has('X-Role')).toBeTrue();
    req.flush(response);
  });

  it('should delete a comment', () => {
    const postId = 1;
    const commentId = 2;
    const response = { message: 'Comment deleted successfully' };

    service.deleteComment(postId, commentId).subscribe((res) => {
      expect(res).toEqual(response);
    });

    const req = httpMock.expectOne(service.BASE_URL + `/${postId}/${commentId}`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.has('X-Role')).toBeTrue();
    req.flush(response);
  });

  it('should handle errors gracefully', () => {
    const errorMessage = 'Something bad happened; please try again later.';
    const mockError = { status: 500, statusText: 'Internal Server Error' };

    service.getComments(1).subscribe({
      next: () => fail('Expected error, but got success response'),
      error: (error) => {
        expect(error.message).toBe(errorMessage);
      },
    });

    const req = httpMock.expectOne(service.BASE_URL + '/1');
    req.flush('Error', mockError);
  });
});
