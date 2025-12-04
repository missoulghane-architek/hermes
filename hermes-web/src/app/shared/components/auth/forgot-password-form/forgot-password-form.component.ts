import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { LabelComponent } from '../../form/label/label.component';
import { InputFieldComponent } from '../../form/input/input-field.component';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-forgot-password-form',
  imports: [
    CommonModule,
    LabelComponent,
    InputFieldComponent,
    RouterModule,
    FormsModule,
  ],
  templateUrl: './forgot-password-form.component.html',
  styles: ``
})
export class ForgotPasswordFormComponent {

  email = '';
  isSubmitting = false;
  isSubmitted = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    if (!this.email) {
      this.errorMessage = 'Please enter your email address';
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.forgotPassword({ email: this.email }).subscribe({
      next: () => {
        this.successMessage = 'Password reset instructions have been sent to your email';
        this.isSubmitting = false;
        this.isSubmitted = true;
      },
      error: (error) => {
        console.error('Forgot password error:', error);
        this.errorMessage = 'Failed to send reset instructions. Please try again.';
        this.isSubmitting = false;
      }
    });
  }
}
