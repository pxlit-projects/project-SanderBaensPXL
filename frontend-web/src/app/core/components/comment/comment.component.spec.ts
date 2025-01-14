import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { DatePipe } from '@angular/common';

import { CommentComponent } from './comment.component';
import { CommentService } from '../../../shared/services/comment.service';
import { AuthService } from '../../../shared/services/auth.service';
import { Comment } from '../../../shared/model/comment.model';

describe('CommentComponent', () => {
  let component: CommentComponent;
  let fixture: ComponentFixture<CommentComponent>;

  let mockCommentService: jasmine.SpyObj<CommentService>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockActivatedRoute: Partial<ActivatedRoute>;

  beforeEach(async () => {
    mockCommentService = jasmine.createSpyObj('CommentService', ['getComments', 'addComment', 'updateComment', 'deleteComment']);
    mockAuthService = jasmine.createSpyObj('AuthService', ['isLoggedIn', 'getUser']);
    mockActivatedRoute = {
      snapshot: {
        paramMap: {
          get: (key: string) => (key === 'id' ? '123' : null),
        } as any,
      }as any,
    };

    mockCommentService.getComments.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [FormsModule, DatePipe, CommentComponent],
      providers: [
        { provide: CommentService, useValue: mockCommentService },
        { provide: AuthService, useValue: mockAuthService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CommentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });
});
