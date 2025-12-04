import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { LabelComponent } from '../../form/label/label.component';
import { CheckboxComponent } from '../../form/input/checkbox.component';
import { InputFieldComponent } from '../../form/input/input-field.component';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../../core/services/auth.service';


@Component({
  selector: 'app-signup-form',
  imports: [
    CommonModule,
    LabelComponent,
    CheckboxComponent,
    InputFieldComponent,
    RouterModule,
    FormsModule,
  ],
  templateUrl: './signup-form.component.html',
  styles: ``
})
export class SignupFormComponent {

  showPassword = false;
  isChecked = false;
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';

  fname = '';
  lname = '';
  email = '';
  password = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  onSignIn() {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.fname || !this.lname || !this.email || !this.password) {
      this.errorMessage = 'Please fill in all required fields';
      return;
    }

    if (!this.isChecked) {
      this.errorMessage = 'You must agree to the Terms and Conditions';
      return;
    }

    this.isSubmitting = true;

    const registerData = {
      username: this.email.split('@')[0],
      firstName: this.fname,
      lastName: this.lname,
      email: this.email,
      password: this.password
    };

    this.authService.register(registerData).subscribe({
      next: () => {
        this.successMessage = 'Your registration request has been successfully submitted! Please check your email to validate your account.';
        this.isSubmitting = false;
        setTimeout(() => {
          this.router.navigate(['/signin']);
        }, 5000);
      },
      error: (error) => {
        console.error('Registration error:', error);
        this.errorMessage = error.error?.message || 'Registration failed. Please try again or contact support.';
        this.isSubmitting = false;
      }
    });
  }
}
