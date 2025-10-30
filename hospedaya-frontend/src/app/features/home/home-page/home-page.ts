import { Component } from '@angular/core';
import { LandingComponent } from '../../../atomic/pages/landing/landing.component';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [LandingComponent],
  templateUrl: './home-page.html',
  styleUrl: './home-page.css',
})
export class HomePage {

}
