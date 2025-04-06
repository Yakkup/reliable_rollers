import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../auth.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, FormsModule],
      declarations: [LoginComponent],
      providers: [
        {
          provide: AuthService,
          useValue: {
            login: jasmine.createSpy('login'),
            setToken: jasmine.createSpy('setToken'),
            needsPasswordChange: jasmine.createSpy('needsPasswordChange'),
          },
        },
        {
          provide: Router,
          useValue: { navigate: jasmine.createSpy('navigate') },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should login successfully and redirect to dashboard', () => {
    const mockResponse = { token: 'fake-jwt-token' };
    (authService.login as jasmine.Spy).and.returnValue(of(mockResponse));
    (authService.needsPasswordChange as jasmine.Spy).and.returnValue(false);

    component.onSubmit();

    expect(authService.login).toHaveBeenCalledWith(component.credentials);
    expect(authService.setToken).toHaveBeenCalledWith('fake-jwt-token');
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
  });

  it('should handle login failure and display error message', () => {
    (authService.login as jasmine.Spy).and.returnValue(throwError(() => ({ error: 'Invalid credentials' })));

    component.onSubmit();

    expect(authService.login).toHaveBeenCalledWith(component.credentials);
    expect(component.errorMessage).toBe('Login failed: Invalid credentials');
  });
});
