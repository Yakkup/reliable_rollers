import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { catchError, of } from 'rxjs';
import {AuthService} from "../../auth/auth.service";

@Component({
  selector: 'app-service-schedule-list',
  templateUrl: './service-schedule-list.component.html',
  styleUrls: ['./service-schedule-list.component.css']
})
export class ServiceScheduleListComponent implements OnInit {

  serviceSchedules: any[] = [];
  selectedScheduleId: number | null = null;
  isAdmin: boolean = false;

  constructor(
    private apiService: ApiService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.fetchSchedules();
    this.isAdmin = this.authService.isAdmin();
  }

  fetchSchedules(): void {
    this.apiService.getServiceSchedules().pipe(
      catchError(err => {
        console.error("Error fetching service schedules:", err);
        return of([]);
      })
    ).subscribe((data: any[]) => {
      console.log("API Response:", data);

      this.serviceSchedules = data.map(schedule => ({
        id: schedule.id,
        garbagePickupDay: schedule.garbagePickupDay,
        nextPickupDate: schedule.nextPickupDate,
        customerName: schedule.customerName,
        customerAddress: schedule.customerAddress
      }));
    });
  }

  deleteSchedule(scheduleId: number): void {
    if (confirm("Are you sure you want to delete this service schedule?")) {
      this.apiService.deleteServiceSchedule(scheduleId).subscribe(
        () => {
          console.log(`✅ Service Schedule ID ${scheduleId} deleted.`);
          this.serviceSchedules = this.serviceSchedules.filter(schedule => schedule.id !== scheduleId);
        },
        (error) => {
          console.error("Error deleting service schedule:", error);
        }
      );
    }
  }

  goToServiceLog(scheduleId: number): void {
    this.selectedScheduleId = scheduleId;
    console.log("Navigating to Service Log for Schedule ID:", this.selectedScheduleId);
    this.router.navigate(['/service-logs', scheduleId]);
  }
}
