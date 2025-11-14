import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../../../shared/components/header/header.component';

@Component({
  selector: 'app-alojamientos-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './alojamientos-admin.component.html',
  styleUrls: ['./alojamientos-admin.component.css']
})
export class AlojamientosAdminComponent {}
