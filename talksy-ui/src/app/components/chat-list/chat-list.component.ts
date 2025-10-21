import { Component, inject, input, InputSignal, OnInit, output } from '@angular/core';
import { ChatResponse, UserResponse } from '../../services/models';
import { DatePipe } from '@angular/common';
import { ChatService, UserService } from '../../services/services';

@Component({
  selector: 'app-chat-list',
  imports: [DatePipe],
  templateUrl: './chat-list.component.html',
  styleUrl: './chat-list.component.scss',
})
export class ChatListComponent implements OnInit {
  chats: InputSignal<ChatResponse[]> = input<ChatResponse[]>([]);
  searchNewContact = input<boolean>();
  contactSearch = input<string>('');
  contacts: Array<UserResponse> = [];
  chatSelected = output<ChatResponse>();
  searchNewContactChange = output<boolean>();
  currentUserId = input<number | null>(); 

  private chatService = inject(ChatService);
  private userService = inject(UserService);

  ngOnInit(): void {
    this.loadUsers();
  }

  chatClicked(chat: ChatResponse): void {
    this.chatSelected.emit(chat);
  }

  selectContact(contact: UserResponse): void {
    const userId = this.currentUserId();
    if (!userId || !contact.id) {
      console.error('Cannot select contact: userId or contact.id is null', { userId, contactId: contact.id });
      return;
    }
    const pendingChat: ChatResponse = {
      // id intentionally omitted -> chat not created yet
      name: contact.nickname || `${contact.firstName ?? ''} ${contact.lastName ?? ''}`.trim(),
      recipientOnline: contact.online,
      lastMessage: '',
      lastMessageTime: contact.lastSeen,
      senderId: userId,
      recipientId: contact.id,
      unreadChatsCount: 0
    };
    // Do not modify chats list here to avoid persisting an empty chat
    this.searchNewContactChange.emit(false);
    this.chatSelected.emit(pendingChat);
  }

  wrapMessage(message: string | undefined): string {
    if (message && message.length <= 20) {
      return message;
    }
    return message?.substring(0, 17) + '...' || '';
  }

  filteredContacts(): UserResponse[] {
    const q = (this.contactSearch() || '').toLowerCase().trim();
    if (!q) return this.contacts;
    return this.contacts.filter(c => (c.nickname || '').toLowerCase().includes(q));
  }

  private loadUsers(): void {
    this.userService.getUsers().subscribe({
      next: (users: UserResponse[]) => {
        this.contacts = users || [];
      },
      error: (err) => {
        console.error('Error loading contacts:', err);
        this.contacts = [];
      }
    });
  }

  private loadChats(): void {
    this.chatService.getAllChatsByRecipientId().subscribe({
      next: (chats: ChatResponse[]) => {
        this.contacts = []; 
        console.log('Loaded chats:', chats);
      },
      error: (err) => {
        console.error('Error loading chats:', err);
      }
    });
  }
}