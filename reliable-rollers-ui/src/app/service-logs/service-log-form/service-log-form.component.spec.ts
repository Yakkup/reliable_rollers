import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceLogFormComponent } from './service-log-form.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {ActivatedRoute} from "@angular/router";
import {of} from "rxjs";

describe('ServiceLogFormComponent', () => {
  let component: ServiceLogFormComponent;
  let fixture: ComponentFixture<ServiceLogFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ServiceLogFormComponent ],
      imports: [ HttpClientTestingModule ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: (key: string) => {
                  if (key === 'scheduleId') return '123'; // Mocking scheduleId
                  if (key === 'logId') return '456'; // Mocking logId
                  return null;
                }
              }
            }
          }
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ServiceLogFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
