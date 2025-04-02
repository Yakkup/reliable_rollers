import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  servicesDueToday: any[] = [];
  servicesDueTomorrow: any[] = [];
  completedServices: { [key: string]: number } = {};

  // ✅ Report Generation Variables
  startDate: string = '';
  endDate: string = '';

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.fetchDashboardData();
  }

  fetchDashboardData(): void {
    this.apiService.getServicesDueToday().subscribe(data => this.servicesDueToday = data);
    this.apiService.getServicesDueTomorrow().subscribe(data => this.servicesDueTomorrow = data);
    this.apiService.getCompletedServicesPerMonth().subscribe(data => this.completedServices = data);
  }

  generateReport(): void {
    if (!this.startDate || !this.endDate) {
      alert("Please select both start and end dates.");
      return;
    }

    this.apiService.exportServiceSchedules(this.startDate, this.endDate).subscribe(response => {
      const blob = new Blob([response], { type: 'text/csv' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `Service_Schedule_Report_${this.startDate}_to_${this.endDate}.csv`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
    }, error => {
      console.error("Error generating report:", error);
      alert("Failed to generate report. Please try again.");
    });
  }
}

