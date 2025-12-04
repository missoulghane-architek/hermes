import { Routes } from '@angular/router';
import { EcommerceComponent } from './components/ecommerce/ecommerce.component';

/**
 * Dashboard Feature Routes
 *
 * Routes pour le module dashboard
 * Protégées par authGuard
 */
export const DASHBOARD_ROUTES: Routes = [
  {
    path: '',
    component: EcommerceComponent,
    title: 'Dashboard | Hermes'
  }
];
