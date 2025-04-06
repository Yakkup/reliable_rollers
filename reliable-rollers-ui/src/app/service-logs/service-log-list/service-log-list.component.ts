import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import {AuthService} from "../../auth/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-service-log-list',
  templateUrl: './service-log-list.component.html',
  styleUrls: ['./service-log-list.component.css']
})
export class ServiceLogListComponent implements OnInit {
  serviceLogs: any[] = [];
  filteredLogs: any[] = [];
  searchTerm: string = '';
  isAdmin: boolean = false;

  constructor(private apiService: ApiService, private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.fetchServiceLogs();
    this.isAdmin = this.authService.isAdmin();
  }

  fetchServiceLogs(): void {
    this.apiService.getServiceLogs().subscribe(
      async (logs) => {
        console.log("Service Logs from API:", logs);

        this.filteredLogs = await Promise.all(logs.map(async (log) => {
          const employeeName = log.employee
            ? `${log.employee.firstName} ${log.employee.lastName}`
            : 'Unknown';

          const customerName = log.customer
            ? `${log.customer.firstName} ${log.customer.lastName}`
            : 'Unknown';

          const customerAddress = log.customer?.address || 'N/A';

          return {
            ...log,
            employeeName,
            customerName,
            customerAddress
          };
        }));

        this.serviceLogs = [...this.filteredLogs];
      },
      (error) => {
        console.error("Error fetching service logs:", error);
      }
    );
  }

  editServiceLog(serviceLogId: number): void {
    this.router.navigate(['/service-logs/edit', serviceLogId]);
  }

  deleteServiceLog(id: number): void {
    if (confirm('Are you sure you want to delete this service log?')) {
      this.apiService.deleteServiceLog(id).subscribe(() => {
        this.serviceLogs = this.serviceLogs.filter(log => log.id !== id);
      });
    }
  }



  searchLogs(): void {
    this.filteredLogs = this.serviceLogs.filter(log =>
      log.customerName?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      log.employeeName?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      log.action?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      log.serviceDate?.includes(this.searchTerm)
    );
  }
}
