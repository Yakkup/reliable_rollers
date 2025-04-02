import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-customer-form',
  templateUrl: './customer-form.component.html',
  styleUrls: ['./customer-form.component.css']
})
export class CustomerFormComponent implements OnInit {
  customer: any = {};
  isEditMode = false;

  daysOfWeek = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

  constructor(
    private apiService: ApiService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const customerId = this.route.snapshot.paramMap.get('id');
    if (customerId) {
      this.isEditMode = true;
      this.apiService.getCustomerById(Number(customerId)).subscribe((data) => {
        this.customer = data;
      });
    }
  }

  onSubmit(): void {
    if (!this.customer.firstName) {
      alert('First name is required.');
      return
    }
    if (!this.customer.lastName) {
      alert('Last name is required.');
      return
    }
    if (!this.customer.address) {
      alert('Address is required.');
      return
    }
    if (!this.customer.garbagePickupDay) {
      alert('Garbage pickup day is required.');
      return
    }

    if (this.isEditMode) {
      this.apiService.updateCustomer(this.customer.id, this.customer).subscribe(() => {
        this.router.navigate(['/customers']);
      });
    } else {
      this.apiService.addCustomer(this.customer).subscribe(() => {
        this.router.navigate(['/customers']);
      });
    }
  }
}

