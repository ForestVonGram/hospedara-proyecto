import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AlojamientoService, AlojamientoCreateRequest } from '../../services/alojamiento.service';
import { ImagenService } from '../../services/imagen.service';

@Component({
  selector: 'app-alojamiento-creation',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './alojamiento-creation.component.html',
  styleUrl: './alojamiento-creation.component.css'
})
export class AlojamientoCreationComponent implements OnInit {
  form!: FormGroup;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';
  
  // Imágenes
  selectedFiles: File[] = [];
  imagePreviews: string[] = [];
  uploadingImages = false;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private alojamientoService: AlojamientoService,
    private router: Router,
    private imagenService: ImagenService
  ) {}

  ngOnInit(): void {
    const user = this.auth.getUser();
    if (!user) {
      // No autenticado
      this.router.navigate(['/login']);
      return;
    }
    if (!user.rol || user.rol !== 'ANFITRION') {
      // Solo anfitriones pueden gestionar alojamientos
      this.router.navigate(['/register-host']);
      return;
    }

    this.form = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(120)]],
      descripcion: ['', [Validators.required, Validators.maxLength(1000)]],
      direccion: ['', [Validators.required, Validators.maxLength(200)]],
      precioPorNoche: [null, [Validators.required, Validators.min(0)]],
      // Campos de UI no enviados aún al backend:
      fechaInicio: [null],
      fechaFin: [null]
    });
  }

  get f() { return this.form.controls; }

  onFilesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files) return;

    const files = Array.from(input.files);
    
    // Validar cantidad
    if (files.length > 10) {
      this.errorMessage = 'Máximo 10 imágenes por alojamiento';
      return;
    }

    // Validar cada archivo
    for (const file of files) {
      if (!this.imagenService.isValidImageFile(file)) {
        this.errorMessage = `${file.name} no es una imagen válida`;
        return;
      }
      if (!this.imagenService.isValidImageSize(file, 10)) {
        this.errorMessage = `${file.name} supera el tamaño máximo de 10MB`;
        return;
      }
    }

    this.selectedFiles = files;
    this.imagePreviews = [];
    this.errorMessage = '';

    // Generar previews
    files.forEach(file => {
      this.imagenService.fileToDataUrl(file).then(dataUrl => {
        this.imagePreviews.push(dataUrl);
      });
    });
  }

  removeImage(index: number): void {
    this.selectedFiles.splice(index, 1);
    this.imagePreviews.splice(index, 1);
  }

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const user = this.auth.getUser();
    if (!user || user.rol !== 'ANFITRION') {
      this.errorMessage = 'Solo los anfitriones pueden publicar alojamientos.';
      return;
    }

    const payload: AlojamientoCreateRequest = {
      nombre: this.f['nombre'].value,
      descripcion: this.f['descripcion'].value,
      direccion: this.f['direccion'].value,
      precioPorNoche: Number(this.f['precioPorNoche'].value),
      anfitrionId: Number(user.id)
    };

    this.isSubmitting = true;
    this.alojamientoService.crearAlojamiento(payload).subscribe({
      next: (resp) => {
        this.successMessage = 'Alojamiento publicado correctamente';
        // Navegar al dashboard del anfitrión o listado
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        // Manejo de errores según backend
        if (typeof err?.error === 'string' && err.error.trim().length > 0) {
          this.errorMessage = err.error;
        } else if (err?.error?.message) {
          this.errorMessage = err.error.message;
        } else if (err.status === 400) {
          this.errorMessage = 'Datos inválidos. Verifica la información ingresada.';
        } else if (err.status === 404) {
          this.errorMessage = 'No se encontró el recurso solicitado (verifica tu cuenta de anfitrión).';
        } else {
          this.errorMessage = 'Error al publicar el alojamiento. Inténtalo nuevamente.';
        }
      },
      complete: () => {
        this.isSubmitting = false;
      }
    });
  }
}
