import { Component } from '@angular/core';
import { AuthPageLayoutComponent } from '../../../../shared/layout/auth-page-layout/auth-page-layout.component';
import { VerifyEmailFormComponent } from '../../../../shared/components/auth/verify-email-form/verify-email-form.component';

@Component({
  selector: 'app-verify-email',
  imports: [
    AuthPageLayoutComponent,
    VerifyEmailFormComponent,
  ],
  templateUrl: './verify-email.component.html',
  styles: ``
})
export class VerifyEmailComponent {

}
