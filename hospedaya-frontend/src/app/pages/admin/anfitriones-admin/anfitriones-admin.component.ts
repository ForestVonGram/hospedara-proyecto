import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../../../shared/components/header/header.component';

@Component({
  selector: 'app-anfitriones-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './anfitriones-admin.component.html',
  styleUrls: ['./anfitriones-admin.component.css']
})
export class AnfitrionesAdminComponent {}
