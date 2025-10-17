import { Component, DestroyRef, inject } from '@angular/core';
import {
  RegistrationRequest
} from '../../services/models';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthenticationService } from '../../services/services';
import { ConfirmAccountComponent } from '../../components/confirm-account/confirm-account.component';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink, ConfirmAccountComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent {

  private authService = inject(AuthenticationService);
  private destroyRef = inject(DestroyRef);

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
  showConfirmAccountModal: boolean = false;

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
    const subscription = this.authService.register({
      body: this.registerRequest
    }).subscribe({
      next: (res) => {
        this.showConfirmAccountModal = true;
      },
      error: (err) => {
        if (err.error?.validationErrors) {
          this.errorMsgs = err.error.validationErrors;
        } else if (err.error?.error) {
          this.addError(err.error.error);
        } else {
          this.addError('An error occurred during registration. Please try again.');
        }
      }
    });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }

  closeConfirmModal() {
    this.showConfirmAccountModal = false;
  }
}
