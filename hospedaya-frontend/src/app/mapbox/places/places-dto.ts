import { LocationDTO } from "../location-dto";

export interface PlaceDTO {
  id: number;
  title: string;
  address: {
    location: LocationDTO;
  };
  images: string[];
}