import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-verify-email-form',
  imports: [
    CommonModule,
    RouterModule,
  ],
  templateUrl: './verify-email-form.component.html',
  styles: ``
})
export class VerifyEmailFormComponent implements OnInit {

  token = '';
  isVerifying = true;
  successMessage = '';
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'] || '';
      if (!this.token) {
        this.errorMessage = 'Invalid or missing verification token';
        this.isVerifying = false;
      } else {
        this.verifyEmail();
      }
    });
  }

  verifyEmail(): void {
   
    this.authService.verifyEmail(this.token).subscribe({
      next: () => {
        this.successMessage = 'Your email has been successfully verified! You can now sign in to your account.';
        this.isVerifying = false;
        setTimeout(() => {
          this.router.navigate(['/signin']);
        }, 3000);
      },
      error: (error) => {
        console.error('Email verification error:', error);
        this.errorMessage = error.error?.message || 'Email verification failed. The verification link may have expired or is invalid.';
        this.isVerifying = false;
      }
    });
  }
}
