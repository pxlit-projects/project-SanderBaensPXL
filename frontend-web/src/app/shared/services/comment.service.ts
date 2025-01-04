import {forwardRef, Inject, Injectable} from '@angular/core';
import {environment} from "../../../environments/environment.development";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {AuthService} from "./auth.service";
import {catchError, throwError} from "rxjs";
import { Comment } from '../model/comment.model';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  BASE_URL: string = environment.api + "/comment/api";

  constructor(
    private http: HttpClient,
    @Inject(forwardRef(() => AuthService)) private authService: AuthService
  ) {}

  getComments(postId: number){
    return this.http.get<Comment[]>(this.BASE_URL + "/" + postId, {headers: this.authService.getHeaders()})
      .pipe(catchError(this.handleError));
  }

  addComment(postId: number, comment: string){
    return this.http.post(this.BASE_URL + "/" + postId, {comment: comment}, {headers: this.authService.getHeaders()})
      .pipe(catchError(this.handleError));
  }

  updateComment(id: number, commentId: number, comment: string){
    return this.http.put(this.BASE_URL + "/" + id + "/" + commentId, {comment: comment}, {headers: this.authService.getHeaders()})
      .pipe(catchError(this.handleError));
  }

  deleteComment(id: number, commentId: number){
    return this.http.delete(this.BASE_URL + "/" + id + "/" + commentId, {headers: this.authService.getHeaders()})
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
