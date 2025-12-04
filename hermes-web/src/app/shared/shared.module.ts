import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// Layout Components
import { AppLayoutComponent } from './layout/app-layout/app-layout.component';
import { AppHeaderComponent } from './layout/app-header/app-header.component';
import { AppSidebarComponent } from './layout/app-sidebar/app-sidebar.component';
import { BackdropComponent } from './layout/backdrop/backdrop.component';
import { AuthPageLayoutComponent } from './layout/auth-page-layout/auth-page-layout.component';

// UI Components
import { DropdownComponent } from './components/ui/dropdown/dropdown.component';
import { ButtonComponent } from './components/ui/button/button.component';
import { AlertComponent } from './components/ui/alert/alert.component';

// Form Components
import { InputFieldComponent } from './components/form/input/input-field.component';
import { CheckboxComponent } from './components/form/input/checkbox.component';
import { LabelComponent } from './components/form/label/label.component';

// Pipes
import { SafeHtmlPipe } from './pipe/safe-html.pipe';

/**
 * Shared Module
 *
 * Ce module contient tous les composants, directives et pipes
 * réutilisables dans toute l'application.
 *
 * Il peut être importé dans n'importe quel feature module.
 */
@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    // Composants standalone
    AppLayoutComponent,
    AppHeaderComponent,
    AppSidebarComponent,
    BackdropComponent,
    AuthPageLayoutComponent,
    DropdownComponent,
    ButtonComponent,
    AlertComponent,
    InputFieldComponent,
    CheckboxComponent,
    LabelComponent,
    SafeHtmlPipe
  ],
  exports: [
    // Modules Angular communs
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    // Layout Components
    AppLayoutComponent,
    AppHeaderComponent,
    AppSidebarComponent,
    BackdropComponent,
    AuthPageLayoutComponent,
    // UI Components
    DropdownComponent,
    ButtonComponent,
    AlertComponent,
    // Form Components
    InputFieldComponent,
    CheckboxComponent,
    LabelComponent,
    // Pipes
    SafeHtmlPipe
  ]
})
export class SharedModule {}
