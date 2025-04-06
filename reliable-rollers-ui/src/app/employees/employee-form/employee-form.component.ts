import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../../services/api.service';

@Component({
  selector: 'app-employee-form',
  templateUrl: './employee-form.component.html',
  styleUrls: ['./employee-form.component.css']
})
export class EmployeeFormComponent implements OnInit {
  employee: any = {
    firstName: '',
    lastName: '',
    email: '',
    role: {roleName: 'ROLE_USER'},
    password: ''
  };
  isEditMode: boolean = false;
  employeeId!: number;
  roles = [
    {roleName: 'ROLE_USER', label: 'User'},
    {roleName: 'ROLE_ADMIN', label: 'Admin'}
  ];

  constructor(private apiService: ApiService, private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.isEditMode = true;
        this.employeeId = +id;
        this.apiService.getEmployeeById(this.employeeId).subscribe(
          (data) => {
            this.employee = {
              id: data.id,
              firstName: data.firstName,
              lastName: data.lastName,
              email: data.email,
              role: data.role?.roleName,
              password: ''
            };
          },
          (error) => {
            console.error("Error fetching employee:", error);
            alert("Failed to load employee details.");
            this.router.navigate(['/employees']);
          }
        );
      }
    });
  }

  saveEmployee(): void {
    if (!this.employee.firstName) {
      alert("First name is required.");
      return;
    }
    if (!this.employee.lastName) {
      alert("Last name is required.");
      return;
    }
    if (!this.employee.email) {
      alert("Email is required.");
      return;
    }
    if (!this.employee.role) {
      alert("Role is required.");
      return;
    }

    if (this.isEditMode) {
      this.apiService.updateEmployee(this.employeeId, this.employee).subscribe(
        () => {
          alert("Employee updated successfully.");
          this.router.navigate(['/employees']);
        },
        (error) => {
          if (error?.message?.includes("Email already exists")) {
            alert("This email is already in use. Please use a different email.");
          } else {
            alert(error.message || "Failed to update employee.");
          }
        }
      );
    } else {
      this.apiService.createEmployee(this.employee).subscribe(
        () => {
          alert("Employee created successfully.");
          this.router.navigate(['/employees']);
        },
        (error) => {
          if (error?.message?.includes("Email already exists")) {
            alert("This email is already in use. Please use a different email.");
          } else {
            alert(error.message || "Failed to create employee.");
          }
        }
      );
    }
  }

  resetPassword(): void {
    const newPassword = prompt("Enter new password:");
    if (newPassword) {
      this.apiService.resetPassword(this.employeeId, newPassword).subscribe({
        next: (response) => {
          alert(response.message || "Password has been reset.");
        },
        error: (err) => {
          console.error("Error resetting password:", err);
          alert("Failed to reset password. Please try again.");
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/employees']);
  }
}
