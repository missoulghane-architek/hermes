import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Property, PropertyStatus } from '../../../core/models/property.model';

@Component({
  selector: 'app-property-card',
  imports: [CommonModule, RouterModule],
  templateUrl: './property-card.component.html',
  styles: ``
})
export class PropertyCardComponent {
  @Input() property!: Property;
  @Output() cardClick = new EventEmitter<Property>();

  get mainImage(): string {
    return this.property.photos && this.property.photos.length > 0
      ? this.property.photos[0]
      : '/assets/images/property-placeholder.jpg';
  }

  get statusBadgeClass(): string {
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

  get statusLabel(): string {
    const labels: Record<PropertyStatus, string> = {
      [PropertyStatus.AVAILABLE]: 'Disponible',
      [PropertyStatus.RENTED]: 'Loué',
      [PropertyStatus.SOLD]: 'Vendu',
      [PropertyStatus.UNAVAILABLE]: 'Indisponible'
    };
    return labels[this.property.status] || this.property.status;
  }

  onCardClick(): void {
    this.cardClick.emit(this.property);
  }
}
