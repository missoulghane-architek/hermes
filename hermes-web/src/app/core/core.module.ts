import { NgModule, Optional, SkipSelf } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HTTP_INTERCEPTORS } from '@angular/common/http';

// Services
import { AuthService } from './services/auth.service';

/**
 * Core Module
 *
 * Ce module contient les services singleton, guards, et interceptors
 * qui doivent être chargés une seule fois dans l'application.
 *
 * Important: Ce module ne doit être importé QUE dans AppModule/AppConfig
 */
@NgModule({
  imports: [CommonModule],
  providers: [
    AuthService,
    // Les interceptors et guards sont maintenant fournis via provideHttpClient dans app.config.ts
  ]
})
export class CoreModule {
  constructor(@Optional() @SkipSelf() parentModule: CoreModule) {
    if (parentModule) {
      throw new Error(
        'CoreModule is already loaded. Import it in the AppModule only'
      );
    }
  }
}
