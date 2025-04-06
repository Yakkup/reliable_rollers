import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceScheduleListComponent } from './service-schedule-list.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('ServiceScheduleListComponent', () => {
  let component: ServiceScheduleListComponent;
  let fixture: ComponentFixture<ServiceScheduleListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ServiceScheduleListComponent ],
      imports: [ HttpClientTestingModule ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ServiceScheduleListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
