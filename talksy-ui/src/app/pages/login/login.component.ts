import { Component, inject } from '@angular/core';
import { AuthenticationRequest } from '../../services/models';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { register } from '../../services/functions';
import { AuthenticationService } from '../../services/services';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  authRequest: AuthenticationRequest = {email: '', password: ''};
  errorMsgs: Array<string> = [];
  loginForm = new FormGroup({
    email: new FormControl(''),
    password: new FormControl(''),
  })
  router = inject(Router);
  authService = inject(AuthenticationService);

  addError(error: string) {
    this.errorMsgs.push(error);  
  }

  login() {
    this.errorMsgs = [];
    this.authRequest.email = this.loginForm.value.email ?? '';
    this.authRequest.password = this.loginForm.value.password ?? '';
    this.authService.register({
      body: this.authRequest
    }).subscribe({
      next: (response) => {
        localStorage.setItem('authToken', response.token ?? '');
        this.router.navigate(['/home']);
      },
      error: (err) => {
        if (err.status === 401) {
          this.addError('Invalid email or password.');
        } else {
          this.addError('An error occurred. Please try again later.');
        }
      }
    });
  }

  navigateToRegister() {
    this.router.navigate(['/register']);
  }


}

