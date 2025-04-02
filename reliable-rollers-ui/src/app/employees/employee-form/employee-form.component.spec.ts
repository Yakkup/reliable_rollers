import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EmployeeFormComponent } from './employee-form.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { FormsModule } from '@angular/forms';

describe('EmployeeFormComponent', () => {
  let component: EmployeeFormComponent;
  let fixture: ComponentFixture<EmployeeFormComponent>;
  let apiService: jasmine.SpyObj<ApiService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const apiServiceSpy = jasmine.createSpyObj('ApiService', ['getEmployeeById', 'createEmployee', 'updateEmployee', 'resetPassword']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      declarations: [EmployeeFormComponent],
      imports: [HttpClientTestingModule, FormsModule],
      providers: [
        { provide: ApiService, useValue: apiServiceSpy },
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of({ get: (key: string) => (key === 'id' ? '1' : null) })
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EmployeeFormComponent);
    component = fixture.componentInstance;
    apiService = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });


  it('should add a new employee when submitting in add mode', () => {
    component.isEditMode = false;
    component.employee = { firstName: 'Jane', lastName: 'Doe', email: 'jane@example.com', role: 'ROLE_USER', password: 'password123' };
    apiService.createEmployee.and.returnValue(of({}));

    component.saveEmployee();

    expect(apiService.createEmployee).toHaveBeenCalledWith(component.employee);
    expect(router.navigate).toHaveBeenCalledWith(['/employees']);
  });

  it('should show an error alert if first name is missing', () => {
    spyOn(window, 'alert');

    component.employee = {
      lastName: 'Doe',
      email: 'test@example.com',
      role: 'ROLE_USER',
      password: 'password123'
    };

    component.saveEmployee();

    expect(window.alert).toHaveBeenCalledWith("First name is required.");
  });


  it('should show an error alert if last name is missing', () => {
    spyOn(window, 'alert');
    component.employee = { firstName: 'Jane', lastName: '', email: 'jane@example.com', role: 'ROLE_USER', password: 'password123' };

    component.saveEmployee();

    expect(window.alert).toHaveBeenCalledWith('Last name is required.');
    expect(apiService.createEmployee).not.toHaveBeenCalled();
  });

  it('should show an error alert if email is missing', () => {
    spyOn(window, 'alert');

    component.employee = {
      firstName: 'John',
      lastName: 'Doe',
      role: 'ROLE_USER',
      password: 'password123'
    };

    component.saveEmployee();

    expect(window.alert).toHaveBeenCalledWith("Email is required.");
  });

  it('should show an error alert if role is missing', () => {
    component.employee = { firstName: 'Jane', lastName: 'Doe', email: 'jane@example.com', role: '', password: 'password123' };
    spyOn(window, 'alert');

    component.saveEmployee();

    expect(window.alert).toHaveBeenCalledWith('Role is required.');
    expect(apiService.createEmployee).not.toHaveBeenCalled();
  });

  it('should prevent adding employee if email already exists', () => {
    spyOn(window, 'alert');
    apiService.createEmployee.and.returnValue(
      throwError(() => new Error("Email already exists"))
    );

    component.employee = {
      firstName: 'John',
      lastName: 'Doe',
      email: 'duplicate@example.com',
      role: 'ROLE_USER',
      password: 'password123'
    };

    component.saveEmployee();

    expect(window.alert).toHaveBeenCalledWith("This email is already in use. Please use a different email.");
  });
});

