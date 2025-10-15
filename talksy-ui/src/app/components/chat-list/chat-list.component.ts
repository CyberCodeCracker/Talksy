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
      console.error('Cannot create chat: userId or contact.id is null', { userId, contactId: contact.id });
      return;
    }
    this.chatService.createChat({
      'sender-id': userId,
      'recipient-id': contact.id
    }).subscribe({
      next: (res) => {
        const chat: ChatResponse = {
          id: res,
          name: contact.firstName + ' ' + contact.lastName,
          recipientOnline: contact.online,
          lastMessageTime: contact.lastSeen,
          senderId: this.currentUserId() ?? undefined,
          recipientId: contact.id
        };
        // insert at beginning
        this.chats().unshift(chat);
        this.searchNewContactChange.emit(false);
        this.chatSelected.emit(chat);
      },
      error: (err) => {
        console.error('Error creating chat:', err);
      }
    });
  }

  wrapMessage(message: string | undefined): string {
    if (message && message.length <= 20) {
      return message;
    }
    return message?.substring(0, 17) + '...' || '';
  }

  private loadUsers(): void {
    this.userService.getUsers().subscribe({
      next: (users: UserResponse[]) => {
        this.contacts = users || [];
        console.log('Loaded contacts:', this.contacts);
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
        this.contacts = []; // Clear contacts when showing chats
        console.log('Loaded chats:', chats);
        // Assuming chats are updated in parent via binding
      },
      error: (err) => {
        console.error('Error loading chats:', err);
      }
    });
  }
}