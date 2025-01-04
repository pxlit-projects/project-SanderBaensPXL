export class Post {
  id: number;
  title: string;
  content: string;
  author: string;
  createdDate: Date;
  accepted: boolean;

  constructor(id: number, title: string, content: string, author: string, createdAt: Date, accepted: boolean) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.author = author;
    this.createdDate = createdAt;
    this.accepted = accepted;
  }
}
