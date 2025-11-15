import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../../shared/components/header/header.component';

@Component({
  selector: 'app-terminos-de-servicio',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './terminos-de-servicio.component.html',
  styleUrls: ['./terminos-de-servicio.component.css']
})
export class TerminosDeServicioComponent {}