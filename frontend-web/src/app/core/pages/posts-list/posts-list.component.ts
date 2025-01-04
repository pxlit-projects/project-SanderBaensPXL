import { Component, OnInit, OnDestroy } from '@angular/core';
import { PostService } from "../../../shared/services/post.service";
import { Post } from "../../../shared/model/post.model";
import {ActivatedRoute, ParamMap, RouterLink} from '@angular/router';
import { Subscription, switchMap } from "rxjs";
import { DatePipe } from "@angular/common";
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-posts-list',
  standalone: true,
  imports: [
    DatePipe,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './posts-list.component.html',
  styleUrl: './posts-list.component.css'
})
export class PostsListComponent implements OnInit, OnDestroy {
  posts: Post[] = [];
  filteredPosts: Post[] = [];
  private routeParamSubscription: Subscription | undefined;
  accepted: string = '';
  filterForm: FormGroup;

  constructor(
    private postService: PostService,
    private route: ActivatedRoute,
    private fb: FormBuilder
  ) {
    this.filterForm = this.fb.group({
      title: [''],
      content: [''],
      author: [''],
      date: [null]
    });
  }

  ngOnInit(): void {
    this.routeParamSubscription = this.route.paramMap.pipe(
      switchMap((params: ParamMap) => {
        const accepted = params.get('accepted') || '';
        return this.postService.getPosts(accepted);
      })
    ).subscribe({
      next: (response) => {
        this.posts = response;
        this.filteredPosts = this.posts;
      },
      error: (error) => {
        console.error('Error fetching posts:', error);
      }
    });

    this.filterForm.valueChanges.subscribe(() => {
      this.filterPosts();
    });
  }

  ngOnDestroy(): void {
    if (this.routeParamSubscription) {
      this.routeParamSubscription.unsubscribe();
    }
  }

  filterPosts(): void {
    const filters = this.filterForm.value;
    this.filteredPosts = this.posts.filter(post => {
      const postDate = new Date(post.createdDate);
      const selectedDate = filters.date ? new Date(filters.date) : null;

      return (
        (!filters.title || post.title.toLowerCase().includes(filters.title.toLowerCase())) &&
        (!filters.content || post.content.toLowerCase().includes(filters.content.toLowerCase())) &&
        (!filters.author || post.author.toLowerCase().includes(filters.author.toLowerCase())) &&
        (!selectedDate || (
          postDate.getFullYear() === selectedDate.getFullYear() &&
          postDate.getMonth() === selectedDate.getMonth() &&
          postDate.getDate() === selectedDate.getDate()
        ))
      );
    });
  }
}
