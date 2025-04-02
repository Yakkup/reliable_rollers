import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';


import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './auth/login/login.component';
import {HTTP_INTERCEPTORS} from "@angular/common/http";
import {AuthInterceptor} from "./auth/auth.interceptor";
import {FormsModule} from "@angular/forms";
import { NavbarComponent } from './shared/navbar/navbar.component';
import { DashboardComponent } from './shared/dashboard/dashboard.component';
import { CustomerListComponent } from './customers/customer-list/customer-list.component';
import { CustomerFormComponent } from './customers/customer-form/customer-form.component';
import { ServiceScheduleListComponent } from './service-schedules/service-schedule-list/service-schedule-list.component';
import { ServiceLogFormComponent } from './service-logs/service-log-form/service-log-form.component';
import { ServiceLogListComponent } from './service-logs/service-log-list/service-log-list.component';
import { EmployeeListComponent } from './employees/employee-list/employee-list.component';
import { EmployeeFormComponent } from './employees/employee-form/employee-form.component';
import { ChangePasswordComponent } from './auth/change-password/change-password.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    NavbarComponent,
    DashboardComponent,
    CustomerListComponent,
    CustomerFormComponent,
    ServiceScheduleListComponent,
    ServiceLogFormComponent,
    ServiceLogListComponent,
    EmployeeListComponent,
    EmployeeFormComponent,
    ChangePasswordComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
