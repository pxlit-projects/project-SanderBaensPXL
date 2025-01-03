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

  public login(role: string, name: string, email: string) {
    this.role = role;
    this.name = name;
    this.email = email;
    localStorage.setItem('role', role);
    localStorage.setItem('name', name);
    localStorage.setItem('email', email);
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
    headers = headers.set('Content-Type', 'application/json');
    headers = headers.set("X-Role", this.role);
    headers = headers.set("X-Name", this.name);
    headers = headers.set("X-Email", this.email);
    return headers;
  }
}
