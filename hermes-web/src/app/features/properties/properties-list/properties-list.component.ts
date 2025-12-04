import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PropertyCardComponent } from '../../../shared/components/property/property-card.component';
import { ButtonComponent } from '../../../shared/components/ui/button/button.component';
import { Property, PropertyService, PropertyStatus, PropertyType } from '../../../core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-properties-list',
  imports: [
    CommonModule,
    PropertyCardComponent,
    ButtonComponent,
    FormsModule
  ],
  templateUrl: './properties-list.component.html',
  styles: ``
})
export class PropertiesListComponent implements OnInit {
  properties: Property[] = [];
  filteredProperties: Property[] = [];

  isLoading: boolean = false;
  error: string | null = null;

  // Pagination
  currentPage: number = 1;
  itemsPerPage: number = 9;
  totalPages: number = 0;

  // Expose Math to template
  Math = Math;

  // Filtres
  searchQuery: string = '';
  filterPropertyType: string = '';
  filterStatus: string = '';

  propertyTypes = Object.values(PropertyType);
  propertyStatuses = Object.values(PropertyStatus);

  constructor(
    private propertyService: PropertyService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProperties();
  }

  loadProperties(): void {
    this.isLoading = true;
    this.error = null;

    this.propertyService.getAll().subscribe({
      next: (properties) => {
        this.properties = properties;
        this.applyFilters();
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des propriétés';
        console.error('Error loading properties:', err);
        this.isLoading = false;
      }
    });
  }

  applyFilters(): void {
    let filtered = [...this.properties];

    if (this.searchQuery) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(p =>
        p.name.toLowerCase().includes(query) ||
        p.description.toLowerCase().includes(query) ||
        p.address.city.toLowerCase().includes(query)
      );
    }

    if (this.filterPropertyType) {
      filtered = filtered.filter(p => p.propertyType === this.filterPropertyType);
    }

    if (this.filterStatus) {
      filtered = filtered.filter(p => p.status === this.filterStatus);
    }

    this.filteredProperties = filtered;
    this.totalPages = Math.ceil(this.filteredProperties.length / this.itemsPerPage);
    this.currentPage = 1;
  }

  getPaginatedProperties(): Property[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.filteredProperties.slice(start, end);
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxPagesToShow = 5;

    let startPage = Math.max(1, this.currentPage - Math.floor(maxPagesToShow / 2));
    let endPage = Math.min(this.totalPages, startPage + maxPagesToShow - 1);

    if (endPage - startPage < maxPagesToShow - 1) {
      startPage = Math.max(1, endPage - maxPagesToShow + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    return pages;
  }

  onPropertyClick(property: Property): void {
    this.router.navigate(['/properties', property.id]);
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  navigateToCreate(): void {
    this.router.navigate(['/properties/new']);
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
