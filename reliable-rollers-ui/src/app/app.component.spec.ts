import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { AuthService } from "./auth/auth.service";

describe('AppComponent', () => {
  let fixture: any;
  let app: any;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientTestingModule
      ],
      declarations: [
        AppComponent
      ],
      providers: [
        {
          provide: AuthService,
          useValue: { isAuthenticated: () => true }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    app = fixture.componentInstance;
  });

  it('should create the app', () => {
    expect(app).toBeTruthy();
  });

  it(`should have as title 'reliable-rollers-ui'`, () => {
    expect(app.title).toEqual('reliable-rollers-ui');
  });
});
