import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-employee-list',
  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.css']
})
export class EmployeeListComponent implements OnInit {
  employees: any[] = [];

  constructor(private apiService: ApiService, private router: Router) {}

  ngOnInit(): void {
    this.fetchEmployees();
  }

  fetchEmployees(): void {
    this.apiService.getEmployees().subscribe(
      (data) => {
        this.employees = data;
      },
      (error) => {
        console.error("Error fetching employees:", error);
      }
    );
  }

  editEmployee(employee: any) {
    this.router.navigate(['/employees/edit', employee.id]);
  }


  deleteEmployee(id: number): void {
    if (confirm('Are you sure you want to delete this employee?')) {
      this.apiService.deleteEmployee(id).subscribe(() => {
        this.fetchEmployees();
      });
    }
  }

  addEmployee(): void {
    this.router.navigate(['/employees/new']);
  }
}

