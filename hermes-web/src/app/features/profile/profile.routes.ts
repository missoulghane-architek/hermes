import { Routes } from '@angular/router';
import { ProfileComponent } from './components/profile/profile.component';

/**
 * Profile Feature Routes
 *
 * Routes pour le module profil utilisateur
 * Protégées par authGuard
 */
export const PROFILE_ROUTES: Routes = [
  {
    path: '',
    component: ProfileComponent,
    title: 'Profile | Hermes'
  }
];
