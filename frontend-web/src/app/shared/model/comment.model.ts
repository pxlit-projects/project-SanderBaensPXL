export class Comment {
  id: number;
  author: string;
  comment: string;
  createdDate: Date;

  constructor(id: number, author: string, comment: string, createdDate: Date) {
    this.id = id;
    this.author = author;
    this.comment = comment;
    this.createdDate = createdDate;
  }
}
