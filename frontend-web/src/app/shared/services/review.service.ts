import {forwardRef, Inject, Injectable} from '@angular/core';
import {environment} from "../../../environments/environment.development";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {AuthService} from "./auth.service";
import {Review} from "../model/review.model";
import {catchError, throwError} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  BASE_URL: string = environment.api + "/review/api";

  constructor(
    private http: HttpClient,
    @Inject(forwardRef(() => AuthService)) private authService: AuthService
  ) {}

  getReviews(postId: number){
    return this.http.get<Review[]>(this.BASE_URL + "/" + postId, {headers: this.authService.getHeaders()})
      .pipe(catchError(this.handleError));
  }

  approve(postId: number){
    return this.http.post(this.BASE_URL + "/approve/" + postId, {}, {headers: this.authService.getHeaders()})
      .pipe(catchError(this.handleError));
  }

  decline(postId: number, comment: string){
    return this.http.post(this.BASE_URL + "/decline/" + postId, {comment: comment}, {headers: this.authService.getHeaders()})
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    if (error.error instanceof ErrorEvent) {
      console.error('An error occurred:', error.error.message);
    } else {
      console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${error.error}`);
    }
    return throwError(() => new Error('Something bad happened; please try again later.'));
  }
}
