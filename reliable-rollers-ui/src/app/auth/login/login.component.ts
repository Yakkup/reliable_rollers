import {Component} from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent  {
  credentials = {
    email: '',
    password: ''
  };

  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    this.authService.login(this.credentials).subscribe({
      next: (response: any) => {
        const token = response.token || response;
        this.authService.setToken(token);

        if (this.authService.needsPasswordChange()) {
          this.router.navigate(['/change-password']);
        } else {
          this.router.navigate(['/dashboard']);
        }
      },
      error: (err) => {
        this.errorMessage = 'Login failed: ' + err.error;
      }
    });
  }

}
