import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {AuthService} from "../auth/auth.service";

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient, private authService: AuthService) { }

  // Customer API's
  getCustomers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/customers`);
  }

  getCustomerById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/customers/${id}`);
  }

  addCustomer(customer: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/customers`, customer);
  }

  updateCustomer(id: number, customer: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/customers/${id}`, customer);
  }

  deleteCustomer(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/customers/${id}`);
  }

  // Service Schedules API's
  getServiceSchedules(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/service-schedules`);
  }

  deleteServiceSchedule(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/service-schedules/${id}`);
  }


  completeServiceLog(scheduleId: number, serviceLog: any): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/service-schedules/${scheduleId}/complete`, serviceLog);
  }

 // Service Log API's
  getServiceLogs(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/service-logs`);
  }

  getServiceLogById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/service-logs/${id}`);
  }

  editServiceLog(id: number, updatedServiceLog: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/service-logs/${id}`, updatedServiceLog);
  }

  deleteServiceLog(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/service-logs/${id}`);
  }

  // Employee API's
  getEmployeeById(employeeId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/employees/${employeeId}`);
  }

  getEmployees(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/employees`);
  }

  createEmployee(employee: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/employees`, {
      ...employee,
      role: { roleName: employee.role }
    });
  }

  resetPassword(id: number, newPassword: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/employees/${id}/reset-password`,
      { newPassword },
      { headers: { 'Content-Type': 'application/json' } }
    );
  }


  updateEmployee(id: number, employee: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/employees/${id}`, {
      ...employee,
      role: { roleName: employee.role }
    });
  }

  deleteEmployee(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/employees/${id}`, {
      headers: { 'Content-Type': 'application/json' }
    });
  }

  // Dashboard API's
  getServicesDueToday(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/dashboard/services-due-today`);
  }

  getServicesDueTomorrow(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/dashboard/services-due-tomorrow`);
  }

  getCompletedServicesPerMonth(): Observable<{ [key: string]: number }> {
    return this.http.get<{ [key: string]: number }>(`${this.apiUrl}/dashboard/completed-services`);
  }

  exportServiceSchedules(startDate: string, endDate?: string): Observable<Blob> {
    const params = endDate ? `startDate=${startDate}&endDate=${endDate}` : `startDate=${startDate}`;
    return this.http.get(`${this.apiUrl}/service-schedules/export?${params}`, {
      responseType: 'blob'
    });
  }

  // LOGIN (Authentication)
  login(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/login`, credentials);
  }

  changePassword(newPassword: string): Observable<any> {
    const token = this.authService.getToken();
    return this.http.put(
      `${this.apiUrl}/auth/change-password`,
      { newPassword },
      { headers: { Authorization: `Bearer ${token}` } }
    );
  }
}

