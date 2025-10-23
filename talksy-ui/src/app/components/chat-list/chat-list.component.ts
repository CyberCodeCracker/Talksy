import {
  Component,
  EventEmitter,
  inject,
  input,
  InputSignal,
  OnInit,
  output,
} from '@angular/core';
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
  currentUserId = input<number | null>();
  searchNewContact = input<boolean>();
  contactSearch = input<string>('');
  backendBaseUrl = input<string>('http://localhost:8080');
  contacts: Array<UserResponse> = [];
  chatSelected = output<ChatResponse>();
  searchNewContactChange = output<boolean>();
  recipientProfilePicture = output<string>();

  private userService = inject(UserService);

  ngOnInit(): void {
    this.loadUsers();
  }

  chatClicked(chat: ChatResponse): void {
    this.chatSelected.emit(chat);
    const profilePicture = this.getUserById(chat.recipientId!)?.profilePicture;
    this.recipientProfilePicture.emit(
      this.buildPictureUrl(profilePicture) || '/assets/images/user.png'
    );
  }

  selectContact(contact: UserResponse): void {
    const userId = this.currentUserId();
    if (!userId || !contact.id) {
      console.error('Cannot select contact: userId or contact.id is null', {
        userId,
        contactId: contact.id,
      });
      return;
    }
    const pendingChat: ChatResponse = {
      name:
        contact.nickname ||
        `${contact.firstName ?? ''} ${contact.lastName ?? ''}`.trim(),
      recipientOnline: contact.online,
      lastMessage: '',
      lastMessageTime: contact.lastSeen,
      senderId: userId,
      recipientId: contact.id,
      unreadChatsCount: 0,
    };
    this.searchNewContactChange.emit(false);
    this.chatSelected.emit(pendingChat);
    this.recipientProfilePicture.emit(
      this.buildPictureUrl(contact.profilePicture) || '/assets/images/user.png'
    );
  }

buildPictureUrl(profilePicture: string | undefined): string {
    // If missing or empty, use default avatar
    if (!profilePicture || !profilePicture.trim()) {
        console.log('Profile picture is empty, using default');
        return '/assets/images/user.png';
    }
    // Handle base64 data (with or without prefix)
    if (profilePicture.startsWith('data:image/')) {
        console.log('Detected base64 image with prefix');
        return profilePicture;
    }
    if (/^[A-Za-z0-9+/=]+$/.test(profilePicture)) {
        console.log('Detected raw base64, adding prefix');
        return `data:image/jpg;base64,${profilePicture}`;
    }
    // Handle absolute URL
    if (/^https?:\/\//i.test(profilePicture)) {
        console.log('Absolute picture URL detected:', profilePicture);
        if (/\/uploads\/users\/user\.png$/i.test(profilePicture)) {
            return '/assets/images/user.png';
        }
        return profilePicture;
    }
    // Handle relative path
    let normalized = profilePicture.replace(/\\/g, '/').replace(/^\.\//, '');
    console.log('Normalized path is:', normalized);
    if (normalized.startsWith('/assets/')) {
        return normalized;
    }
    if (normalized.endsWith('/uploads/users/user.png')) {
        return '/assets/images/user.png';
    }
    if (!normalized.startsWith('/')) {
        normalized = '/' + normalized;
    }
    return `${this.backendBaseUrl()}${normalized}`;
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
    return this.contacts.filter((c) =>
      (c.nickname || '').toLowerCase().includes(q)
    );
  }

  getUserById(id: number): UserResponse | undefined {
    return this.contacts.find((user) => user.id === id);
  }

  private loadUsers(): void {
    this.userService.getUsers().subscribe({
      next: (users: UserResponse[]) => {
        this.contacts = users || [];
      },
      error: (err) => {
        console.error('Error loading contacts:', err);
        this.contacts = [];
      },
    });
  }
}
