import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from "@angular/common/http";
import { ReviewService } from './review.service';
import { AuthService } from './auth.service';
import { Review } from '../model/review.model';
import { environment } from '../../../environments/environment';

describe('ReviewService', () => {
  let service: ReviewService;
  let httpMock: HttpTestingController;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  const mockReviews: Review[] = [
    new Review('Author 1', 'Comment for review 1'),
    new Review('Author 2', 'Comment for review 2'),
  ];

  beforeEach(() => {
    const authSpy = jasmine.createSpyObj('AuthService', ['getHeaders']);

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        ReviewService,
        { provide: AuthService, useValue: authSpy },
      ],
    });

    service = TestBed.inject(ReviewService);
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

  it('should fetch reviews', () => {
    const postId = 1;

    service.getReviews(postId).subscribe((reviews) => {
      expect(reviews).toEqual(mockReviews);
    });

    const req = httpMock.expectOne(`${service.BASE_URL}/${postId}`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.has('X-Role')).toBeTrue();
    req.flush(mockReviews);
  });

  it('should approve a post', () => {
    const postId = 1;
    const response = { message: 'Post approved successfully' };

    service.approve(postId).subscribe((res) => {
      expect(res).toEqual(response);
    });

    const req = httpMock.expectOne(`${service.BASE_URL}/approve/${postId}`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({});
    expect(req.request.headers.has('X-Role')).toBeTrue();
    req.flush(response);
  });

  it('should decline a post', () => {
    const postId = 1;
    const comment = 'Decline comment';
    const response = { message: 'Post declined successfully' };

    service.decline(postId, comment).subscribe((res) => {
      expect(res).toEqual(response);
    });

    const req = httpMock.expectOne(`${service.BASE_URL}/decline/${postId}`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ comment: comment });
    expect(req.request.headers.has('X-Role')).toBeTrue();
    req.flush(response);
  });

  it('should handle errors gracefully', () => {
    const errorMessage = 'Something bad happened; please try again later.';
    const mockError = { status: 500, statusText: 'Internal Server Error' };

    service.getReviews(1).subscribe({
      next: () => fail('Expected error, but got success response'),
      error: (error) => {
        expect(error.message).toBe(errorMessage);
      },
    });

    const req = httpMock.expectOne(`${service.BASE_URL}/1`);
    req.flush('Error', mockError);
  });
});
