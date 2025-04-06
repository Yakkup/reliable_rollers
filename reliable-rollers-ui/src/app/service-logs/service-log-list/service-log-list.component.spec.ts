import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceLogListComponent } from './service-log-list.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('ServiceLogListComponent', () => {
  let component: ServiceLogListComponent;
  let fixture: ComponentFixture<ServiceLogListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ServiceLogListComponent ],
      imports: [ HttpClientTestingModule ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ServiceLogListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
