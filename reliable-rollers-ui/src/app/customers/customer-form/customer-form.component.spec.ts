import {ComponentFixture, TestBed} from '@angular/core/testing';
import {CustomerFormComponent} from './customer-form.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../../services/api.service';
import {of} from 'rxjs';
import {FormsModule} from '@angular/forms';

describe('CustomerFormComponent', () => {
  let component: CustomerFormComponent;
  let fixture: ComponentFixture<CustomerFormComponent>;
  let apiService: jasmine.SpyObj<ApiService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const apiServiceSpy = jasmine.createSpyObj('ApiService', ['getCustomerById', 'addCustomer', 'updateCustomer']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      declarations: [CustomerFormComponent],
      imports: [HttpClientTestingModule, FormsModule],
      providers: [
        {provide: ApiService, useValue: apiServiceSpy},
        {provide: Router, useValue: routerSpy},
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: (key: string) => (key === 'id' ? '456' : null)
              }
            }
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CustomerFormComponent);
    component = fixture.componentInstance;
    apiService = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;

    apiService.getCustomerById.and.returnValue(of({
      id: 456,
      firstName: 'John',
      lastName: 'Doe',
      address: '123 Street',
      garbagePickupDay: 'Monday'
    }));
    apiService.addCustomer.and.returnValue(of({}));
    apiService.updateCustomer.and.returnValue(of({}));

    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize in edit mode if customer ID is provided', () => {
    const mockCustomer = {
      id: 456,
      firstName: 'John',
      lastName: 'Doe',
      address: '123 Street',
      garbagePickupDay: 'Monday'
    };
    apiService.getCustomerById.and.returnValue(of(mockCustomer));

    fixture.detectChanges();

    expect(component.isEditMode).toBeTrue();
    expect(apiService.getCustomerById).toHaveBeenCalledWith(456);
    expect(component.customer).toEqual(mockCustomer);
  });

  it('should add a new customer when submitting in add mode', () => {
    component.isEditMode = false;
    component.customer = {firstName: 'Jane', lastName: 'Doe', address: '456 Avenue', garbagePickupDay: 'Tuesday'};
    apiService.addCustomer.and.returnValue(of({}));

    component.onSubmit();

    expect(apiService.addCustomer).toHaveBeenCalledWith(component.customer);
    expect(router.navigate).toHaveBeenCalledWith(['/customers']);
  });

  it('should update an existing customer when submitting in edit mode', () => {
    component.isEditMode = true;
    component.customer = {
      id: 456,
      firstName: 'Jane',
      lastName: 'Doe',
      address: '456 Avenue',
      garbagePickupDay: 'Tuesday'
    };
    apiService.updateCustomer.and.returnValue(of({}));

    component.onSubmit();

    expect(apiService.updateCustomer).toHaveBeenCalledWith(456, component.customer);
    expect(router.navigate).toHaveBeenCalledWith(['/customers']);
  });

  it('should validate that required fields are filled before submitting', () => {
    component.customer = {firstName: '', lastName: '', address: '', garbagePickupDay: ''};

    spyOn(window, 'alert');

    component.onSubmit();

    expect(apiService.addCustomer).not.toHaveBeenCalled();
    expect(apiService.updateCustomer).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should not call API if required fields are missing when updating', () => {
    component.isEditMode = true;
    component.customer = {id: 456, firstName: '', lastName: 'Doe', address: '456 Avenue', garbagePickupDay: 'Tuesday'};

    spyOn(window, 'alert');

    component.onSubmit();

    expect(apiService.updateCustomer).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should show an error alert if first name is missing', () => {
    component.customer = {firstName: '', lastName: 'Doe', address: '456 Avenue', garbagePickupDay: 'Tuesday'};
    spyOn(window, 'alert');

    component.onSubmit();

    expect(window.alert).toHaveBeenCalledWith('First name is required.');
    expect(apiService.addCustomer).not.toHaveBeenCalled();
    expect(apiService.updateCustomer).not.toHaveBeenCalled();
  });

  it('should show an error alert if last name is missing', () => {
    component.customer = {firstName: 'Jane', lastName: '', address: '456 Avenue', garbagePickupDay: 'Tuesday'};
    spyOn(window, 'alert');

    component.onSubmit();

    expect(window.alert).toHaveBeenCalledWith('Last name is required.');
    expect(apiService.addCustomer).not.toHaveBeenCalled();
    expect(apiService.updateCustomer).not.toHaveBeenCalled();
  });

  it('should show an error alert if address is missing', () => {
    component.customer = {firstName: 'Jane', lastName: 'Doe', address: '', garbagePickupDay: 'Tuesday'};
    spyOn(window, 'alert');

    component.onSubmit();

    expect(window.alert).toHaveBeenCalledWith('Address is required.');
    expect(apiService.addCustomer).not.toHaveBeenCalled();
    expect(apiService.updateCustomer).not.toHaveBeenCalled();
  });

  it('should show an error alert if garbage pickup day is missing', () => {
    component.customer = {firstName: 'Jane', lastName: 'Doe', address: '456 Avenue', garbagePickupDay: ''};
    spyOn(window, 'alert');

    component.onSubmit();

    expect(window.alert).toHaveBeenCalledWith('Garbage pickup day is required.');
    expect(apiService.addCustomer).not.toHaveBeenCalled();
    expect(apiService.updateCustomer).not.toHaveBeenCalled();
  });
});

