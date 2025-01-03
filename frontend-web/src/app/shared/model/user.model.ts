export class User {
  role: string;
  name: string;
  email: string;

  constructor(role: string, name: string, email: string) {
    this.role = role;
    this.name = name;
    this.email = email;
  }
}
