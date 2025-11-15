import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../../shared/components/header/header.component';

@Component({
  selector: 'app-prensa',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './prensa.component.html',
  styleUrls: ['./prensa.component.css']
})
export class PrensaComponent {}
