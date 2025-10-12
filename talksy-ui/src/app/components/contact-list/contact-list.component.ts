import { Component, inject, input, InputSignal, output } from '@angular/core';
import { ChatResponse, UserResponse } from '../../services/models';
import { ChatService, UserService } from '../../services/services';

@Component({
  selector: 'app-contact-list',
  imports: [],
  templateUrl: './contact-list.component.html',
  styleUrl: './contact-list.component.scss',
})
export class ContactListComponent {
  chats: InputSignal<ChatResponse[]> = input<ChatResponse[]>([]);
  searchNewContact = output<boolean>();

  onSearchContact(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchNewContact.emit(input.checked);
  }

  onExitSearchContact(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchNewContact.emit(input.checked);
  }
}
