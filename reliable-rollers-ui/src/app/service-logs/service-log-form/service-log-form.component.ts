import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-service-log-form',
  templateUrl: './service-log-form.component.html',
  styleUrls: ['./service-log-form.component.css']
})
export class ServiceLogFormComponent implements OnInit {
  serviceScheduleId!: number;
  logId!: number | null;
  isEditMode: boolean = false;

  serviceLog: any = {
    serviceDate: new Date().toISOString().split('T')[0],
    action: '',
    notes: ''
  };

  constructor(
    private apiService: ApiService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.serviceScheduleId = Number(this.route.snapshot.paramMap.get('scheduleId'));
    this.logId = Number(this.route.snapshot.paramMap.get('logId'));

    if (this.logId) {
      this.isEditMode = true;
      this.loadServiceLog(this.logId);
    }
  }

  loadServiceLog(logId: number): void {
    this.apiService.getServiceLogById(logId).subscribe(
      (log) => {
        this.serviceLog = log;
      },
      (error) => {
        console.error("Error fetching service log:", error);
      }
    );
  }

  submitServiceLog(): void {
    if (!this.serviceLog.action) {
      alert("Please select an action before submitting.");
      return;
    }

    if (this.isEditMode) {
      // ✅ Editing existing log
      this.apiService.editServiceLog(this.logId!, this.serviceLog).subscribe(
        () => {
          console.log("Service Log updated successfully.");
          this.router.navigate(['/service-logs']);
        },
        (error) => {
          console.error("Error updating service log:", error);
        }
      );
    } else {
      // ✅ Creating new log
      this.apiService.completeServiceLog(this.serviceScheduleId, this.serviceLog).subscribe(
        () => {
          console.log("Service Log successfully submitted.");
          this.router.navigate(['/service-schedules']);
        },
        (error) => {
          console.error("Error submitting service log:", error);
        }
      );
    }
  }
}
