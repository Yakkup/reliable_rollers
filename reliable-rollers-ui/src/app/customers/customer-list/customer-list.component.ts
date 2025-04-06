import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-customer-list',
  templateUrl: './customer-list.component.html',
  styleUrls: ['./customer-list.component.css']
})
export class CustomerListComponent implements OnInit {
  customers: any[] = [];
  filteredCustomers: any[] = [];
  searchTerm: string = '';
  isAdmin: boolean = false;

  constructor(private apiService: ApiService, private router: Router, private authService: AuthService) {}

  ngOnInit(): void {
    this.loadCustomers();
    this.isAdmin = this.authService.isAdmin();
  }

  loadCustomers(): void {
    this.apiService.getCustomers().subscribe({
      next: (data: any[]) => {
        console.log('Received customers:', data);
        this.customers = data;
        this.filteredCustomers = data;
      },
      error: (error) => {
        console.error('Error fetching customers', error);
      }
    });
  }

  filterCustomers(): void {
    const term = this.searchTerm.toLowerCase();
    this.filteredCustomers = this.customers.filter(customer =>
      customer.firstName.toLowerCase().includes(term) ||
      customer.lastName.toLowerCase().includes(term) ||
      customer.email.toLowerCase().includes(term) ||
      customer.phone.toLowerCase().includes(term) ||
      customer.address.toLowerCase().includes(term) ||
      customer.garbagePickupDay.toLowerCase().includes(term)
    );
  }

  deleteCustomer(id: number): void {
    if (confirm('Are you sure you want to delete this customer?')) {
      this.apiService.deleteCustomer(id).subscribe({
        next: () => {
          console.log(`Customer with ID ${id} deleted successfully`);
          this.customers = this.customers.filter(customer => customer.id !== id);
          this.filteredCustomers = this.filteredCustomers.filter(customer => customer.id !== id);
        },
        error: (error) => {
          console.error('Error deleting customer', error);
        }
      });
    }
  }
}
