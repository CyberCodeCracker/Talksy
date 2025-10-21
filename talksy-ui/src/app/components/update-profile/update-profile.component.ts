import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserResponse } from '../../services/models';

@Component({
  selector: 'app-update-profile',
  imports: [CommonModule, FormsModule],
  templateUrl: './update-profile.component.html',
  styleUrl: './update-profile.component.scss'
})
export class UpdateProfileComponent {
  @Input() visible: boolean = false;
  @Input() currentUser?: UserResponse;
  @Output() close = new EventEmitter<void>();
  @Output() saved = new EventEmitter<{ nickname?: string; file?: File | null }>();

  nickname: string = '';
  previewUrl: string | null = null;
  selectedFile: File | null = null;

  ngOnChanges(): void {
    this.nickname = this.currentUser?.nickname || '';
    this.previewUrl = null;
    this.selectedFile = null;
  }

  onBackdropClick(e: MouseEvent) {
    if ((e.target as HTMLElement).classList.contains('modal-backdrop')) {
      this.close.emit();
    }
  }

  onFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) {
      this.selectedFile = null;
      this.previewUrl = null;
      return;
    }
    const file = input.files[0];
    this.selectedFile = file;
    const reader = new FileReader();
    reader.onload = () => (this.previewUrl = (reader.result as string) ?? null);
    reader.readAsDataURL(file);
  }

  submit() {
    this.saved.emit({ nickname: this.nickname?.trim() || undefined, file: this.selectedFile });
  }
}
