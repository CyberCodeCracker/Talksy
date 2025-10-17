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
  // Current search state provided by parent (defaults to false)
  isSearching = input<boolean>(false);
  // Emit search text and filter changes to parent
  searchTextChange = output<string>();
  filterChange = output<'all' | 'unread'>();

  onSearchContact(): void {
    console.log('Contact list on search contact:true');
    this.searchNewContact.emit(true);
  }

  onExitSearchContact(): void {
    console.log('Contact list on exit search contact:false');
    this.searchNewContact.emit(false);
  }

  onSearchInput(value: string): void {
    this.searchTextChange.emit(value);
  }

  setFilter(filter: 'all' | 'unread'): void {
    this.filterChange.emit(filter);
  }
}
