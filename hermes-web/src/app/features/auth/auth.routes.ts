import { Routes } from '@angular/router';
import { SignInComponent } from './components/sign-in/sign-in.component';
import { SignUpComponent } from './components/sign-up/sign-up.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { VerifyEmailComponent } from './components/verify-email/verify-email.component';

/**
 * Auth Feature Routes
 *
 * Routes pour le module d'authentification
 * Accessible sans authentification
 */
export const AUTH_ROUTES: Routes = [
  {
    path: 'signin',
    component: SignInComponent,
    title: 'Sign In | Hermes'
  },
  {
    path: 'signup',
    component: SignUpComponent,
    title: 'Sign Up | Hermes'
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordComponent,
    title: 'Forgot Password | Hermes'
  },
  {
    path: 'reset-password',
    component: ResetPasswordComponent,
    title: 'Reset Password | Hermes'
  },
  {
    path: 'verify-email',
    component: VerifyEmailComponent,
    title: 'Verify Email | Hermes'
  }
];
