import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../../shared/components/header/header.component';

@Component({
  selector: 'app-politicas-y-privacidad',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './politicas-y-privacidad.component.html',
  styleUrls: ['./politicas-y-privacidad.component.css']
})
export class PoliticasYPrivacidadComponent {}