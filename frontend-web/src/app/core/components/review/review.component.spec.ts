import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewComponent } from './review.component';
import {ActivatedRoute} from "@angular/router";
import {ReviewService} from "../../../shared/services/review.service";
import {of} from "rxjs";
import {FormsModule} from "@angular/forms";

describe('ReviewComponent', () => {
  let component: ReviewComponent;
  let fixture: ComponentFixture<ReviewComponent>;

  let mockReviewService: jasmine.SpyObj<ReviewService>;
  let mockActivatedRoute: jasmine.SpyObj<ActivatedRoute>;

  beforeEach(async () => {
    mockReviewService = jasmine.createSpyObj('ReviewService', ['getReviews', 'approve', 'decline']);
    mockActivatedRoute = {
      paramMap: of({ get: () => '' })
    } as any;

    await TestBed.configureTestingModule({
      imports: [FormsModule, ReviewComponent],
      providers: [
        { provide: ReviewService, useValue: mockReviewService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReviewComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
