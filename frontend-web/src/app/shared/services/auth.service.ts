import { Injectable } from '@angular/core';
import { User } from '../model/user.model';
import {HttpHeaders} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private role: string = "";
  private name: string = "";
  private email: string = "";

  constructor() {
    this.loadUserFromLocalStorage();
  }

  private loadUserFromLocalStorage() {
    const role = localStorage.getItem('role');
    if(role) {
      this.role = role;
    }
    const name = localStorage.getItem('name');
    if(name) {
      this.name = name;
    }
    const email = localStorage.getItem('email');
    if(email) {
      this.email = email;
    }
  }

  public getUser(): User {
    return new User(this.role, this.name, this.email);
  }

  public login(user: User) {
    this.role = user.role
    this.name = user.name;
    this.email = user.email;
    localStorage.setItem('role', user.role);
    localStorage.setItem('name', user.name);
    localStorage.setItem('email', user.email);
  }

  public logout() {
    this.role = "";
    this.name = "";
    this.email = "";
    localStorage.removeItem('role');
    localStorage.removeItem('name');
    localStorage.removeItem('email');
  }

  public getHeaders(): HttpHeaders {
    let headers = new HttpHeaders();
    headers = headers.set("X-Role", this.role);
    headers = headers.set("X-Name", this.name);
    headers = headers.set("X-Email", this.email);
    return headers;
  }

  public checkLogin(): boolean {
    return this.role !== "";
  }

  public checkRole(role: string): boolean {
    return this.role === role;
  }
}
