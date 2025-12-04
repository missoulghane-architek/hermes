import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ButtonComponent } from '../../../shared/components/ui/button/button.component';
import { LabelComponent } from '../../../shared/components/form/label/label.component';
import { PropertyService, Property, CreatePropertyRequest, UpdatePropertyRequest, PropertyType, PropertyStatus } from '../../../core';

@Component({
  selector: 'app-property-form',
  imports: [
    CommonModule,
    FormsModule,
    ButtonComponent,
    LabelComponent
  ],
  templateUrl: './property-form.component.html',
  styles: ``
})
export class PropertyFormComponent implements OnInit {
  propertyId: string | null = null;
  property: Property | null = null;
  isEditMode = false;

  // Wizard state
  currentStep: number = 1;
  totalSteps: number = 3;

  formData: CreatePropertyRequest = {
    name: '',
    description: '',
    propertyType: PropertyType.HOUSE,
    street: '',
    city: '',
    postalCode: '',
    country: '',
    area: 0,
    status: PropertyStatus.AVAILABLE,
    photos: []
  };

  photoInput: string = '';
  isSubmitting = false;
  isLoading = false;
  error: string | null = null;

  propertyTypes = Object.values(PropertyType);
  propertyStatuses = Object.values(PropertyStatus);

  constructor(
    private propertyService: PropertyService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.propertyId = this.route.snapshot.paramMap.get('id');

    if (this.propertyId) {
      this.isEditMode = true;
      this.loadProperty(this.propertyId);
    }
  }

  loadProperty(id: string): void {
    this.isLoading = true;
    this.error = null;

    this.propertyService.getById(id).subscribe({
      next: (property) => {
        this.property = property;
        this.formData = {
          name: property.name,
          description: property.description,
          propertyType: property.propertyType,
          street: property.address.street,
          city: property.address.city,
          postalCode: property.address.postalCode,
          country: property.address.country,
          area: property.area,
          status: property.status,
          photos: [...property.photos]
        };
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement de la propriété';
        console.error('Error loading property:', err);
        this.isLoading = false;
      }
    });
  }

  addPhoto(): void {
    if (this.photoInput.trim()) {
      this.formData.photos.push(this.photoInput.trim());
      this.photoInput = '';
    }
  }

  removePhoto(index: number): void {
    this.formData.photos.splice(index, 1);
  }

  // Wizard navigation
  nextStep(): void {
    if (!this.validateCurrentStep()) {
      return;
    }

    if (this.currentStep < this.totalSteps) {
      this.currentStep++;
      this.error = null;
    }
  }

  previousStep(): void {
    if (this.currentStep > 1) {
      this.currentStep--;
      this.error = null;
    }
  }

  goToStep(step: number): void {
    // Only allow going to previous steps or next step if current is valid
    if (step < this.currentStep) {
      this.currentStep = step;
      this.error = null;
    } else if (step === this.currentStep + 1 && this.validateCurrentStep()) {
      this.currentStep = step;
      this.error = null;
    }
  }

  getStepTitle(step: number): string {
    switch (step) {
      case 1:
        return 'Informations générales';
      case 2:
        return 'Adresse';
      case 3:
        return 'Photos';
      default:
        return '';
    }
  }

  isStepValid(step: number): boolean {
    switch (step) {
      case 1:
        return !!(this.formData.name.trim() &&
                  this.formData.description.trim() &&
                  this.formData.area > 0);
      case 2:
        return !!(this.formData.street.trim() &&
                  this.formData.city.trim() &&
                  this.formData.postalCode.trim() &&
                  this.formData.country.trim());
      case 3:
        return true; // Photos are optional
      default:
        return false;
    }
  }

  validateCurrentStep(): boolean {
    this.error = null;

    switch (this.currentStep) {
      case 1:
        if (!this.formData.name.trim()) {
          this.error = 'Le nom est requis';
          return false;
        }
        if (!this.formData.description.trim()) {
          this.error = 'La description est requise';
          return false;
        }
        if (this.formData.area <= 0) {
          this.error = 'La surface doit être supérieure à 0';
          return false;
        }
        break;
      case 2:
        if (!this.formData.street.trim()) {
          this.error = 'La rue est requise';
          return false;
        }
        if (!this.formData.city.trim()) {
          this.error = 'La ville est requise';
          return false;
        }
        if (!this.formData.postalCode.trim()) {
          this.error = 'Le code postal est requis';
          return false;
        }
        if (!this.formData.country.trim()) {
          this.error = 'Le pays est requis';
          return false;
        }
        break;
      case 3:
        // Photos are optional
        break;
    }

    return true;
  }

  handleSubmit(): void {
    if (!this.validateForm()) {
      return;
    }

    this.isSubmitting = true;
    this.error = null;

    if (this.isEditMode && this.propertyId) {
      const updateData: UpdatePropertyRequest = { ...this.formData };
      this.propertyService.update(this.propertyId, updateData).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.router.navigate(['/properties']);
        },
        error: (err) => {
          this.error = 'Erreur lors de la mise à jour de la propriété';
          console.error('Error updating property:', err);
          this.isSubmitting = false;
        }
      });
    } else {
      this.propertyService.create(this.formData).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.router.navigate(['/properties']);
        },
        error: (err) => {
          this.error = 'Erreur lors de la création de la propriété';
          console.error('Error creating property:', err);
          this.isSubmitting = false;
        }
      });
    }
  }

  validateForm(): boolean {
    if (!this.formData.name.trim()) {
      this.error = 'Le nom est requis';
      return false;
    }
    if (!this.formData.description.trim()) {
      this.error = 'La description est requise';
      return false;
    }
    if (!this.formData.street.trim()) {
      this.error = 'La rue est requise';
      return false;
    }
    if (!this.formData.city.trim()) {
      this.error = 'La ville est requise';
      return false;
    }
    if (!this.formData.postalCode.trim()) {
      this.error = 'Le code postal est requis';
      return false;
    }
    if (!this.formData.country.trim()) {
      this.error = 'Le pays est requis';
      return false;
    }
    if (this.formData.area <= 0) {
      this.error = 'La surface doit être supérieure à 0';
      return false;
    }
    return true;
  }

  handleCancel(): void {
    this.router.navigate(['/properties']);
  }

  getPropertyTypeLabel(type: PropertyType): string {
    const labels: Record<PropertyType, string> = {
      [PropertyType.HOUSE]: 'Maison',
      [PropertyType.APARTMENT]: 'Appartement',
      [PropertyType.VILLA]: 'Villa',
      [PropertyType.LAND]: 'Terrain',
      [PropertyType.COMMERCIAL]: 'Commercial'
    };
    return labels[type] || type;
  }

  getStatusLabel(status: PropertyStatus): string {
    const labels: Record<PropertyStatus, string> = {
      [PropertyStatus.AVAILABLE]: 'Disponible',
      [PropertyStatus.RENTED]: 'Loué',
      [PropertyStatus.SOLD]: 'Vendu',
      [PropertyStatus.UNAVAILABLE]: 'Indisponible'
    };
    return labels[status] || status;
  }
}
