import { Component } from '@angular/core';
import {
  AuthenticationRequest,
  RegistrationRequest,
} from '../../services/models';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent {

  authRequest: RegistrationRequest = {
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

  }
}
