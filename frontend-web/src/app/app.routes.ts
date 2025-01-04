import { Routes } from '@angular/router';
import {PostsListComponent} from "./core/pages/posts-list/posts-list.component";
import {AddPostComponent} from "./core/pages/add-post/add-post.component";
import {PostComponent} from "./core/pages/post/post.component";

export const routes: Routes = [
  { path: 'posts/:accepted', component: PostsListComponent },
  { path: 'post/add', component: AddPostComponent },
  { path: 'post/:id', component: PostComponent }
];
