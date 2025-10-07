import { Component } from '@angular/core';
import { AuthenticationRequest } from '../../services/models';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

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

  addError(error: string) {
    this.errorMsgs.push(error);  
  }

  onSubmit() {

  }

}

