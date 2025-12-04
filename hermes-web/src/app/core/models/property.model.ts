export enum PropertyType {
  HOUSE = 'HOUSE',
  APARTMENT = 'APARTMENT',
  VILLA = 'VILLA',
  LAND = 'LAND',
  COMMERCIAL = 'COMMERCIAL'
}

export enum PropertyStatus {
  AVAILABLE = 'AVAILABLE',
  RENTED = 'RENTED',
  SOLD = 'SOLD',
  UNAVAILABLE = 'UNAVAILABLE'
}

export interface PropertyAddress {
  street: string;
  city: string;
  postalCode: string;
  country: string;
}

export interface Property {
  id: string;
  ownerId: string;
  name: string;
  description: string;
  propertyType: PropertyType;
  address: PropertyAddress;
  area: number;
  status: PropertyStatus;
  photos: string[];
  createdAt: string;
  updatedAt: string;
}

export interface CreatePropertyRequest {
  name: string;
  description: string;
  propertyType: PropertyType;
  street: string;
  city: string;
  postalCode: string;
  country: string;
  area: number;
  status: PropertyStatus;
  photos: string[];
}

export interface UpdatePropertyRequest {
  name?: string;
  description?: string;
  propertyType?: PropertyType;
  street?: string;
  city?: string;
  postalCode?: string;
  country?: string;
  area?: number;
  status?: PropertyStatus;
  photos?: string[];
}
