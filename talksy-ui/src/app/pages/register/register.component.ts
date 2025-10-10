import { Component, inject } from '@angular/core';
import {
  AuthenticationRequest,
  RegistrationRequest,
} from '../../services/models';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthenticationService } from '../../services/services';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent {

  authService = inject(AuthenticationService);
  router = inject(Router);


  registerRequest: RegistrationRequest = {
    email: '',
    firstName: '',
    lastName: '',
    nickname: '',
    password: '',
  };

  registrationForm = new FormGroup({
    email: new FormControl(''),
    firstName: new FormControl(''),
    lastName: new FormControl(''),
    nickname: new FormControl(''),
    password: new FormControl(''),
    confirmPassword: new FormControl(''),
  })

  errorMsgs: Array<string> = [];

  addError(error: string) {
    this.errorMsgs.push(error);  
  }

  onSubmit() {
    this.errorMsgs = [];
    if (this.registrationForm.value.password !== this.registrationForm.value.confirmPassword) {
      this.addError("Passwords do not match");
      return;
    }
    this.registerRequest.email = this.registrationForm.value.email ?? '';
    this.registerRequest.firstName = this.registrationForm.value.firstName ?? '';
    this.registerRequest.lastName = this.registrationForm.value.lastName ?? '';
    this.registerRequest.nickname = this.registrationForm.value.nickname ?? '';
    this.registerRequest.password = this.registrationForm.value.password ?? '';
    this.authService.register({
      body: this.registerRequest
    }).subscribe({
      next: (res) => {
        console.log(res);
        
      },
      error: (err) => {

      }
    })
  }


}
