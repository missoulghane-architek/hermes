import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Property, PropertyService, PropertyStatus, PropertyType } from '../../../core';
import { ButtonComponent } from '../../../shared/components/ui/button/button.component';

@Component({
  selector: 'app-property-detail',
  imports: [CommonModule, ButtonComponent],
  templateUrl: './property-detail.component.html',
  styles: ``
})
export class PropertyDetailComponent implements OnInit {
  property: Property | null = null;
  isLoading: boolean = true;
  error: string | null = null;

  currentPhotoIndex: number = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private propertyService: PropertyService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadProperty(id);
    } else {
      this.error = 'ID de propriété manquant';
      this.isLoading = false;
    }
  }

  loadProperty(id: string): void {
    this.isLoading = true;
    this.error = null;

    this.propertyService.getById(id).subscribe({
      next: (property) => {
        this.property = property;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement de la propriété';
        console.error('Error loading property:', err);
        this.isLoading = false;
      }
    });
  }

  get mainPhoto(): string {
    if (this.property?.photos && this.property.photos.length > 0) {
      return this.property.photos[this.currentPhotoIndex];
    }
    return '/assets/images/property-placeholder.jpg';
  }

  get hasMultiplePhotos(): boolean {
    return this.property?.photos && this.property.photos.length > 1 || false;
  }

  previousPhoto(): void {
    if (this.property?.photos && this.property.photos.length > 0) {
      this.currentPhotoIndex = (this.currentPhotoIndex - 1 + this.property.photos.length) % this.property.photos.length;
    }
  }

  nextPhoto(): void {
    if (this.property?.photos && this.property.photos.length > 0) {
      this.currentPhotoIndex = (this.currentPhotoIndex + 1) % this.property.photos.length;
    }
  }

  goToPhoto(index: number): void {
    this.currentPhotoIndex = index;
  }

  getStatusLabel(): string {
    if (!this.property) return '';
    const labels: Record<PropertyStatus, string> = {
      [PropertyStatus.AVAILABLE]: 'Disponible',
      [PropertyStatus.RENTED]: 'Loué',
      [PropertyStatus.SOLD]: 'Vendu',
      [PropertyStatus.UNAVAILABLE]: 'Indisponible'
    };
    return labels[this.property.status] || this.property.status;
  }

  getStatusBadgeClass(): string {
    if (!this.property) return '';
    switch (this.property.status) {
      case PropertyStatus.AVAILABLE:
        return 'bg-green-50 dark:bg-green-500/15 text-green-700 dark:text-green-500';
      case PropertyStatus.RENTED:
        return 'bg-blue-50 dark:bg-blue-500/15 text-blue-700 dark:text-blue-500';
      case PropertyStatus.SOLD:
        return 'bg-gray-50 dark:bg-gray-500/15 text-gray-700 dark:text-gray-500';
      case PropertyStatus.UNAVAILABLE:
        return 'bg-red-50 dark:bg-red-500/15 text-red-700 dark:text-red-500';
      default:
        return 'bg-gray-50 dark:bg-gray-500/15 text-gray-700 dark:text-gray-500';
    }
  }

  getPropertyTypeLabel(): string {
    if (!this.property) return '';
    const labels: Record<PropertyType, string> = {
      [PropertyType.HOUSE]: 'Maison',
      [PropertyType.APARTMENT]: 'Appartement',
      [PropertyType.VILLA]: 'Villa',
      [PropertyType.LAND]: 'Terrain',
      [PropertyType.COMMERCIAL]: 'Commercial'
    };
    return labels[this.property.propertyType] || this.property.propertyType;
  }

  goBack(): void {
    this.router.navigate(['/properties']);
  }

  editProperty(): void {
    if (this.property) {
      this.router.navigate(['/properties/edit', this.property.id]);
    }
  }

  deleteProperty(): void {
    if (!this.property) return;

    if (confirm('Êtes-vous sûr de vouloir supprimer cette propriété ?')) {
      this.propertyService.delete(this.property.id).subscribe({
        next: () => {
          this.router.navigate(['/properties']);
        },
        error: (err) => {
          this.error = 'Erreur lors de la suppression de la propriété';
          console.error('Error deleting property:', err);
        }
      });
    }
  }
}
