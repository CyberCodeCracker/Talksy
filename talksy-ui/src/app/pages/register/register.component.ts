import { Component, DestroyRef, inject } from '@angular/core';
import { RegistrationRequest } from '../../services/models';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthenticationService } from '../../services/services';
import { ConfirmAccountComponent } from '../../components/confirm-account/confirm-account.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, ConfirmAccountComponent],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  private authService = inject(AuthenticationService);
  private destroyRef = inject(DestroyRef);

  registerRequest: RegistrationRequest = {
    email: '',
    firstName: '',
    lastName: '',
    nickname: '',
    password: ''
  };

  registrationForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    firstName: new FormControl('', [Validators.required, Validators.minLength(3), Validators.maxLength(10)]),
    lastName: new FormControl('', [Validators.required, Validators.minLength(3), Validators.maxLength(10)]),
    nickname: new FormControl('', [Validators.required, Validators.minLength(3), Validators.maxLength(15)]),
    password: new FormControl('', [Validators.required, Validators.minLength(8), Validators.maxLength(15), Validators.pattern(/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/)]),
    confirmPassword: new FormControl('', [Validators.required])
  });

  errorMsgs: Array<string> = [];
  showConfirmAccountModal: boolean = false;
  selectedFile: File | null = null;
  previewUrl: string | null = null;

  addError(error: string) {
    this.errorMsgs.push(error);
  }

  private addErrorUnique(error: string) {
    if (!this.errorMsgs.includes(error)) {
      this.errorMsgs.push(error);
    }
  }

  private collectValidationErrors(): string[] {
    const messages: string[] = [];
    const labels: Record<string, string> = {
      email: 'Email',
      firstName: 'First name',
      lastName: 'Last name',
      nickname: 'Nickname',
      password: 'Password',
      confirmPassword: 'Confirm password'
    };

    Object.entries(this.registrationForm.controls).forEach(([name, control]) => {
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
      if (errors['maxlength']) {
        messages.push(`${label} must be at most ${errors['maxlength'].requiredLength} characters.`);
      }
      if (errors['pattern'] && name === 'password') {
        messages.push('Password must contain at least one letter and one number.');
      }
    });
    return messages;
  }

  private collectControlErrors(name: string): string[] {
    const labels: Record<string, string> = {
      email: 'Email',
      firstName: 'First name',
      lastName: 'Last name',
      nickname: 'Nickname',
      password: 'Password',
      confirmPassword: 'Confirm password'
    };
    const control = this.registrationForm.get(name);
    const messages: string[] = [];
    if (!control) return messages;
    const errors = control.errors;
    if (!errors) return messages;
    const label = labels[name] ?? name;
    if (errors['required']) messages.push(`${label} is required.`);
    if (errors['email']) messages.push('Email must be a valid email address.');
    if (errors['minlength']) messages.push(`${label} must be at least ${errors['minlength'].requiredLength} characters.`);
    if (errors['maxlength']) messages.push(`${label} must be at most ${errors['maxlength'].requiredLength} characters.`);
    if (errors['pattern'] && name === 'password') messages.push('Password must contain at least one letter and one number.');
    return messages;
  }

  validateControl(name: 'email' | 'firstName' | 'lastName' | 'nickname' | 'password' | 'confirmPassword') {
    const msgs = this.collectControlErrors(name);
    msgs.forEach(m => this.addErrorUnique(m));
    if (name === 'confirmPassword') {
      const pwd = this.registrationForm.value.password;
      const confirm = this.registrationForm.value.confirmPassword;
      if (pwd !== confirm) this.addErrorUnique('Passwords do not match');
    }
  }

  onSubmit() {
    this.errorMsgs = [];
    if (this.registrationForm.invalid) {
      this.errorMsgs = this.collectValidationErrors();
    }

    if (this.registrationForm.value.password !== this.registrationForm.value.confirmPassword) {
      this.addError('Passwords do not match');
    }

    if (this.errorMsgs.length > 0) {
      return;
    }

    const formData = new FormData();
    this.registerRequest.email = this.registrationForm.value.email ?? '';
    this.registerRequest.firstName = this.registrationForm.value.firstName ?? '';
    this.registerRequest.lastName = this.registrationForm.value.lastName ?? '';
    this.registerRequest.nickname = this.registrationForm.value.nickname ?? '';
    this.registerRequest.password = this.registrationForm.value.password ?? '';

    formData.append('request', new Blob([JSON.stringify(this.registerRequest)], { type: 'application/json' }));

    if (this.selectedFile) {
      formData.append('profilePicture', this.selectedFile, this.selectedFile.name);
    }

    const subscription = this.authService.register({
      body: {
        request: this.registerRequest,
        profilePicture: this.selectedFile ? this.selectedFile : undefined
      }
    }).subscribe({
      next: () => {
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

  onFileChange(target: EventTarget | null): void {
    const input = target as HTMLInputElement;
    if (!input || !input.files || input.files.length === 0) return;
    const file = input.files[0];
    this.selectedFile = file;
    const reader = new FileReader();
    reader.onload = () => {
      this.previewUrl = typeof reader.result === 'string' ? reader.result : null;
    };
    reader.readAsDataURL(file);
  }
}