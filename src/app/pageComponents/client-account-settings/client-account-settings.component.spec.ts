import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientAccountSettingsComponent } from './client-account-settings.component';

describe('ClientAccountSettingsComponent', () => {
  let component: ClientAccountSettingsComponent;
  let fixture: ComponentFixture<ClientAccountSettingsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClientAccountSettingsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClientAccountSettingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
