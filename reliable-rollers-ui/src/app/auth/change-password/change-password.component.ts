import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent {
  newPassword: string = '';
  confirmPassword: string = '';
  errorMessage: string = '';

  constructor(private apiService: ApiService, private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = "Passwords do not match!";
      return;
    }

    this.apiService.changePassword(this.newPassword).subscribe({
      next: (response) => {
        console.log("Password changed successfully.", response);

        alert("Password changed successfully. Please log in again.");

        this.authService.clearPasswordChangeFlag();
        this.authService.logout();

        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error("Error changing password:", err);

        if (err.status === 200) {
          this.authService.clearPasswordChangeFlag();
          this.authService.logout();
          this.router.navigate(['/login']);
        } else if (err.error && err.error.message) {
          this.errorMessage = err.error.message;
        } else if (typeof err.error === 'string') {
          this.errorMessage = err.error;
        } else {
          this.errorMessage = "An error occurred while changing the password.";
        }
      }
    });
  }



}
