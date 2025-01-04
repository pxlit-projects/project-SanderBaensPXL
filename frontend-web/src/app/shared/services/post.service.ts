import {forwardRef, Inject, Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {AuthService} from "./auth.service";
import {environment} from "../../../environments/environment.development";
import { PostRequest } from '../model/post-request.model';
import {Post} from "../model/post.model";
import {catchError, throwError} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class PostService {
  BASE_URL: string = environment.api + "/post/api";

  constructor(
    private http: HttpClient,
    @Inject(forwardRef(() => AuthService)) private authService: AuthService
  ) {}

  addPost(postRequest: PostRequest){
    return this.http.post(this.BASE_URL, postRequest, {headers: this.authService.getHeaders()})
      .pipe(catchError(this.handleError));
  }

  updatePost(id: number, postRequest: PostRequest){
    return this.http.put(this.BASE_URL + "/" + id, postRequest, {headers: this.authService.getHeaders()})
      .pipe(catchError(this.handleError));
  }

  getPosts(accepted: string){
    return this.http.get<Post[]>(this.BASE_URL + "/" + accepted, {headers: this.authService.getHeaders()})
      .pipe(catchError(this.handleError));
  }

  getPostById(id: number){
    return this.http.get<Post>(this.BASE_URL + "/" + id, {headers: this.authService.getHeaders()})
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
