import { Component, inject, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../services/services';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MaterialModule } from '../../material.module';
import { CodeInputModule } from 'angular-code-input';

@Component({
  selector: 'app-confirm-account',
  imports: [ReactiveFormsModule, MaterialModule, MatDialogModule, CodeInputModule],
  templateUrl: './confirm-account.component.html',
  styleUrl: './confirm-account.component.scss'
})
export class ConfirmAccountComponent {

  @Output() close = new EventEmitter<void>();

  message: string = "";
  isSubmitted: boolean = false;
  isOkay: boolean = false;
  submitted: boolean = false;
  errorMsgs: Array<string> = [];

  private router = inject(Router);
  private authService = inject(AuthenticationService);

  addError(error: string) {
    this.errorMsgs.push(error);
  }

  onCodeCompleted(code: string) {
    this.errorMsgs = [];
    console.log('Confirming account with token:', code);
    this.authService.confirmAccount({
      token: code
    }).subscribe({
      next: () => {
        this.isSubmitted = true;
        this.submitted = true;
        this.isOkay = true;
        this.message = "Your account has been successfully confirmed. You can now log in.";  
      },
      error: (err) => {
        console.error('Confirmation error:', err);
        this.isSubmitted = true;
        this.submitted = true;
        this.isOkay = false;  
        if (err.status === 403) {
          this.addError('Access forbidden. This endpoint may require authentication or the token is invalid.');
          this.message = "Authentication error - please contact support.";
        } else if (err.status === 400) {
          this.addError('Invalid or expired token. Please request a new confirmation email.');
          this.message = "There was an error confirming your account.";
        } else {
          this.addError(`An error occurred (${err.status}). Please try again later.`);
          this.message = "There was an error confirming your account.";
        }
      }
    })
  }

  navigateToLogin() {
    this.router.navigate(['/login']);
  }

  navigateToRegister() {
    this.closeModal();
    this.router.navigate(['/register']);
  }

  closeModal() {
    this.close.emit();
  }
}
