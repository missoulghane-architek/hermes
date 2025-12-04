import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ButtonComponent } from '../../shared/components/ui/button/button.component';
import { TableDropdownComponent } from '../../shared/components/common/table-dropdown/table-dropdown.component';
import { ModalComponent } from '../../shared/components/ui/modal/modal.component';
import { PropertyService, Property, PropertyType, PropertyStatus } from '../../core';

interface Sort {
  key: keyof Property;
  asc: boolean;
}

@Component({
  selector: 'app-properties',
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    ButtonComponent,
    TableDropdownComponent,
    ModalComponent
  ],
  templateUrl: './properties.component.html',
  styles: ``
})
export class PropertiesComponent implements OnInit {
  properties: Property[] = [];
  filteredProperties: Property[] = [];
  selected: string[] = [];
  sort: Sort = { key: 'name', asc: true };
  page: number = 1;
  perPage: number = 7;
  showFilter: boolean = false;

  searchQuery: string = '';
  filterPropertyType: string = '';
  filterStatus: string = '';

  isLoading: boolean = false;
  error: string | null = null;

  isDeleteModalOpen: boolean = false;
  propertyToDelete: Property | null = null;

  propertyTypes = Object.values(PropertyType);
  propertyStatuses = Object.values(PropertyStatus);

  constructor(
    private propertyService: PropertyService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadProperties();
  }

  loadProperties() {
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

  applyFilters() {
    let filtered = [...this.properties];

    if (this.searchQuery) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(p =>
        p.name.toLowerCase().includes(query) ||
        p.description.toLowerCase().includes(query) ||
        p.address.city.toLowerCase().includes(query) ||
        p.address.street.toLowerCase().includes(query)
      );
    }

    if (this.filterPropertyType) {
      filtered = filtered.filter(p => p.propertyType === this.filterPropertyType);
    }

    if (this.filterStatus) {
      filtered = filtered.filter(p => p.status === this.filterStatus);
    }

    this.filteredProperties = filtered;
    this.page = 1;
  }

  onSearchChange() {
    this.applyFilters();
  }

  sortedProperties(): Property[] {
    return [...this.filteredProperties].sort((a, b) => {
      let valA: any = a[this.sort.key];
      let valB: any = b[this.sort.key];

      if (this.sort.key === 'area') {
        valA = Number(valA);
        valB = Number(valB);
      }

      if (valA < valB) return this.sort.asc ? -1 : 1;
      if (valA > valB) return this.sort.asc ? 1 : -1;
      return 0;
    });
  }

  paginatedProperties(): Property[] {
    const start = (this.page - 1) * this.perPage;
    return this.sortedProperties().slice(start, start + this.perPage);
  }

  totalPages(): number {
    return Math.ceil(this.filteredProperties.length / this.perPage);
  }

  goToPage(n: number): void {
    if (n >= 1 && n <= this.totalPages()) {
      this.page = n;
    }
  }

  prevPage(): void {
    if (this.page > 1) {
      this.page--;
    }
  }

  nextPage(): void {
    if (this.page < this.totalPages()) {
      this.page++;
    }
  }

  toggleSelect(id: string): void {
    this.selected = this.selected.includes(id)
      ? this.selected.filter((i) => i !== id)
      : [...this.selected, id];
  }

  toggleAll(): void {
    const ids = this.paginatedProperties().map((p) => p.id);
    this.selected = this.isAllSelected()
      ? this.selected.filter((id) => !ids.includes(id))
      : [...new Set([...this.selected, ...ids])];
  }

  isAllSelected(): boolean {
    const ids = this.paginatedProperties().map((p) => p.id);
    return ids.length > 0 && ids.every((id) => this.selected.includes(id));
  }

  startItem(): number {
    return this.filteredProperties.length === 0 ? 0 : (this.page - 1) * this.perPage + 1;
  }

  endItem(): number {
    return Math.min(this.page * this.perPage, this.filteredProperties.length);
  }

  sortBy(key: keyof Property): void {
    this.sort = {
      key,
      asc: this.sort.key === key ? !this.sort.asc : true,
    };
  }

  toggleFilter(): void {
    this.showFilter = !this.showFilter;
  }

  navigateToCreate(): void {
    this.router.navigate(['/properties/new']);
  }

  navigateToEdit(property: Property): void {
    this.router.navigate(['/properties/edit', property.id]);
  }

  openDeleteModal(property: Property): void {
    this.propertyToDelete = property;
    this.isDeleteModalOpen = true;
  }

  closeDeleteModal(): void {
    this.isDeleteModalOpen = false;
    this.propertyToDelete = null;
  }

  confirmDelete(): void {
    if (this.propertyToDelete) {
      this.propertyService.delete(this.propertyToDelete.id).subscribe({
        next: () => {
          this.loadProperties();
          this.closeDeleteModal();
        },
        error: (err) => {
          console.error('Error deleting property:', err);
          this.error = 'Erreur lors de la suppression de la propriété';
        }
      });
    }
  }

  getStatusBadgeClass(status: PropertyStatus): string {
    switch (status) {
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
