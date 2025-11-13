import { Injectable } from '@angular/core';
import { PlaceDTO } from './places-dto';

@Injectable({
  providedIn: 'root'
})
export class PlacesService {
  private places: PlaceDTO[] = [
    {
      id: 1,
      title: 'Apartamento en el Centro',
      address: {
        location: { latitud: 4.60971, longitud: -74.08175 }
      },
      images: ['https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=400']
    },
    {
      id: 2,
      title: 'Casa en Chapinero',
      address: {
        location: { latitud: 4.63889, longitud: -74.06333 }
      },
      images: ['https://images.unsplash.com/photo-1568605114967-8130f3a36994?q=80&w=400']
    },
    {
      id: 3,
      title: 'Loft Moderno en Usaquén',
      address: {
        location: { latitud: 4.69597, longitud: -74.03063 }
      },
      images: ['https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?q=80&w=400']
    },
    {
      id: 4,
      title: 'Estudio en La Candelaria',
      address: {
        location: { latitud: 4.59778, longitud: -74.07565 }
      },
      images: ['https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?q=80&w=400']
    },
    {
      id: 5,
      title: 'Apartamento con Vista en Rosales',
      address: {
        location: { latitud: 4.66417, longitud: -74.05583 }
      },
      images: ['https://images.unsplash.com/photo-1512917774080-9991f1c4c750?q=80&w=400']
    }
  ];

  getAll(): PlaceDTO[] {
    return this.places;
  }
}