import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { DashboardComponent } from './shared/dashboard/dashboard.component';
import { CustomerListComponent } from './customers/customer-list/customer-list.component';
import { CustomerFormComponent } from './customers/customer-form/customer-form.component';
import { ServiceScheduleListComponent } from './service-schedules/service-schedule-list/service-schedule-list.component';
import {ServiceLogFormComponent} from "./service-logs/service-log-form/service-log-form.component";
import {ServiceLogListComponent} from "./service-logs/service-log-list/service-log-list.component";
import {EmployeeListComponent} from "./employees/employee-list/employee-list.component";
import {EmployeeFormComponent} from "./employees/employee-form/employee-form.component";
import {AuthGuard} from "./auth/auth.guard";
import {ChangePasswordComponent} from "./auth/change-password/change-password.component";

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'change-password', component: ChangePasswordComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'customers', component: CustomerListComponent },
  { path: 'customers/add', component: CustomerFormComponent },
  { path: 'customers/edit/:id', component: CustomerFormComponent },
  { path: 'service-schedules', component: ServiceScheduleListComponent },
  { path: 'service-logs/edit/:logId', component: ServiceLogFormComponent },
  { path: 'service-logs/:scheduleId', component: ServiceLogFormComponent },
  { path: 'service-logs', component: ServiceLogListComponent },
  { path: 'employees', component: EmployeeListComponent, canActivate: [AuthGuard] },
  { path: 'employees/new', component: EmployeeFormComponent, canActivate: [AuthGuard] },
  { path: 'employees/edit/:id', component: EmployeeFormComponent, canActivate: [AuthGuard] },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

