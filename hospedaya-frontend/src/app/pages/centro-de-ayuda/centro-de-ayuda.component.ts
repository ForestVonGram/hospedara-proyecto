import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../../shared/components/header/header.component';

@Component({
  selector: 'app-centro-de-ayuda',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './centro-de-ayuda.component.html',
  styleUrls: ['./centro-de-ayuda.component.css']
})
export class CentroDeAyudaComponent {}