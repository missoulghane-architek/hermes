import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { catchError, map, of } from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  if (authService.hasRefreshToken()) {
    return authService.refreshToken().pipe(
      map(() => true),
      catchError(() => {
        authService.logoutAndRedirect();
        router.navigate(['/signin'], { queryParams: { returnUrl: state.url } });
        return of(false);
      })
    );
  }

  router.navigate(['/signin'], { queryParams: { returnUrl: state.url } });
  return false;
};
