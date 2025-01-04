import {Component, inject} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {User} from "../../../shared/model/user.model";
import {AuthService} from "../../../shared/services/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  router: Router = inject(Router);
  authService: AuthService = inject(AuthService);
  user: User = new User("", "", "");

  onSubmit(form: any): void {
    if(form.valid){
      this.authService.login(this.user);
      this.router.navigate(['/posts', 'accepted']);
    }
  }
}
