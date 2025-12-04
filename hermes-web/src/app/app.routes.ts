import { Routes } from '@angular/router';
import { AppLayoutComponent } from './shared/layout/app-layout/app-layout.component';
import { NotFoundComponent } from './pages/other-page/not-found/not-found.component';
import { authGuard } from './core/guards/auth.guard';

/**
 * Application Routes
 *
 * Configuration du routing principal avec lazy loading
 * Les features sont chargées à la demande pour optimiser les performances
 */
export const routes: Routes = [
  // Routes protégées - Nécessitent une authentification
  {
    path: '',
    component: AppLayoutComponent,
    canActivate: [authGuard],
    children: [
      // Dashboard (page d'accueil)
      {
        path: '',
        loadChildren: () =>
          import('./features/dashboard/dashboard.routes').then(m => m.DASHBOARD_ROUTES),
        pathMatch: 'full'
      },

      // Profile
      {
        path: 'profile',
        loadChildren: () =>
          import('./features/profile/profile.routes').then(m => m.PROFILE_ROUTES)
      },

      // Properties
      {
        path: 'properties',
        loadChildren: () =>
          import('./features/properties/properties.routes').then(m => m.PROPERTIES_ROUTES)
      },

      // Templates - Forms, Tables, Charts, UI Elements, etc.
      {
        path: '',
        loadChildren: () =>
          import('./features/templates/templates.routes').then(m => m.TEMPLATES_ROUTES)
      }
    ]
  },

  // Auth Routes - Accessible sans authentification
  {
    path: '',
    loadChildren: () =>
      import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },

  // Error pages
  {
    path: '**',
    component: NotFoundComponent,
    title: 'Page Not Found | Hermes'
  }
];
