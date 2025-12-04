import { Routes } from '@angular/router';
import { PropertiesComponent } from './properties.component';
import { PropertiesListComponent } from './properties-list/properties-list.component';
import { PropertyFormComponent } from './property-form/property-form.component';
import { PropertyDetailComponent } from './property-detail/property-detail.component';

export const PROPERTIES_ROUTES: Routes = [
  {
    path: '',
    component: PropertiesListComponent,
    title: 'Biens Immobiliers | Hermes'
  },
  {
    path: 'table',
    component: PropertiesComponent,
    title: 'Biens Immobiliers (Table) | Hermes'
  },
  {
    path: 'new',
    component: PropertyFormComponent,
    title: 'Nouveau Bien Immobilier | Hermes'
  },
  {
    path: 'edit/:id',
    component: PropertyFormComponent,
    title: 'Modifier Bien Immobilier | Hermes'
  },
  {
    path: ':id',
    component: PropertyDetailComponent,
    title: 'Détail Bien Immobilier | Hermes'
  }
];
