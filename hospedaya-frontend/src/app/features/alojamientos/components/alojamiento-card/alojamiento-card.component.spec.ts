import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlojamientoCardComponent } from './alojamiento-card.component';

describe('AlojamientoCardComponent', () => {
  let component: AlojamientoCardComponent;
  let fixture: ComponentFixture<AlojamientoCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlojamientoCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AlojamientoCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
