import { Component, DestroyRef, inject } from '@angular/core';
import { AuthenticationRequest } from '../../services/models';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthenticationService } from '../../services/services';
import { HttpClient } from '@angular/common/http';
import { TokenService } from '../../services/token.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  providers: [AuthenticationService, HttpClient]
})
export class LoginComponent {

  authRequest: AuthenticationRequest = {email: '', password: ''};
  errorMsgs: Array<string> = [];
  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(8)]),
  });

  private router = inject(Router);
  private authService = inject(AuthenticationService);
  private tokenService = inject(TokenService);
  private destroyRef = inject(DestroyRef);

  addError(error: string) {
    this.errorMsgs.push(error);  
  }

  private collectValidationErrors(): string[] {
    const messages: string[] = [];
    const labels: Record<string, string> = { email: 'Email', password: 'Password' };
    Object.entries(this.loginForm.controls).forEach(([name, control]) => {
      const errors = control.errors;
      if (!errors) return;
      const label = labels[name] ?? name;
      if (errors['required']) {
        messages.push(`${label} is required.`);
      }
      if (errors['email']) {
        messages.push('Email must be a valid email address.');
      }
      if (errors['minlength']) {
        messages.push(`${label} must be at least ${errors['minlength'].requiredLength} characters.`);
      }
    });
    return messages;
  }

  login() {
    this.errorMsgs = [];
    if (this.loginForm.invalid) {
      this.errorMsgs = this.collectValidationErrors();
      return;
    }
    this.authRequest.email = this.loginForm.value.email ?? '';
    this.authRequest.password = this.loginForm.value.password ?? '';
    const subscription = this.authService.login({
      body: this.authRequest
    }).subscribe({
      next: (response) => {
        if (response.access_token && response.refresh_token) {
          this.tokenService.setTokens(response.access_token, response.refresh_token);
          this.router.navigate(['home']);
        } else {
          this.addError('Invalid response from server.');
        }
      },
      error: (err) => {
        if (err.status === 401) {
          this.addError('Invalid email or password.');
        } else if (err.status === 403) {
          this.addError('Account not activated. Please check your email.');
        } else {
          this.addError('An error occurred. Please try again later.');
        }
      }
    });
    this.destroySubscription(subscription);
  }

  private destroySubscription(subscription: Subscription) {
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    })
  };

}

