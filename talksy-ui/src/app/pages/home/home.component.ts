import { Component, inject, input, OnInit, signal } from '@angular/core';
import { ChatListComponent } from '../../components/chat-list/chat-list.component';
import { ChatResponse, MessageRequest, MessageResponse, UserResponse } from '../../services/models';
import { ChatService, MessageService, UserService } from '../../services/services';
import { ContactListComponent } from '../../components/contact-list/contact-list.component';
import { TokenService } from '../../services/token.service';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { PickerComponent } from '@ctrl/ngx-emoji-mart';
import { FormsModule } from "@angular/forms";
import { EmojiData } from '@ctrl/ngx-emoji-mart/ngx-emoji';

@Component({
  selector: 'app-home',
  imports: [ChatListComponent, ContactListComponent, DatePipe, PickerComponent, FormsModule],
  standalone: true,
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit {
  chats: Array<ChatResponse> = [];
  selectedChat: ChatResponse = {};
  chatMessages: Array<MessageResponse> = [];
  currentUserId: number | null = null;
  showEmojis: boolean = false;
  messageContent: string = '';

  private chatService = inject(ChatService);
  private messageService = inject(MessageService);
  private tokenService = inject(TokenService);
  private userService = inject(UserService);
  private router = inject(Router);

  searchNewContact = signal(false);

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadChats();
  }

  updateSearchContact(newValue: boolean) {
    this.searchNewContact.set(newValue);
  }

  isSelfMessage(message: MessageResponse): boolean {
    return message.senderId === this.currentUserId;
  }

  userProfile() {}

  logout() {
    this.tokenService.clearTokens();
    this.router.navigate(['login']);
  }

  chatSelected(chatResponse: ChatResponse) {
    this.selectedChat = chatResponse;
    this.getAllChatMessages(chatResponse.id ? chatResponse.id : 0);
    this.setMessagesToSeen();
    console.log(this.selectedChat.name);
    this.selectedChat.unreadChatsCount = 0;
  }

  onClick() {
    this.setMessagesToSeen();
  }

  sendMessage() {
    if (this.messageContent) {
      const messageRequest: MessageRequest = {
        chatId: this.selectedChat.id,
        senderId: this.getSenderId(),
        receiverId: this.getRecipientId(),
        content: this.messageContent,
        messageType: 'TEXT'
      };
      this.messageService.saveMessage({
        body: messageRequest
      }).subscribe({
        next: () => {
          const message: MessageResponse = {
            senderId: this.getSenderId(),
            recipientId: this.getRecipientId(),
            message: this.messageContent,
            type: 'TEXT',
            state: 'SENT',
            createdAt: new Date().toString()
          };
          this.selectedChat.lastMessage = this.messageContent;
          this.chatMessages.push(message);
          this.messageContent = '';
          this.showEmojis = false;
        }
      })
    }
  }

  onSelectEmojis(emojiSelected: any) {
    const emoji: EmojiData = emojiSelected.emoji;
    this.messageContent += emoji.name;
  }

  keyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      this.sendMessage();
    }
  }

  uploadMedia(target: EventTarget | null): void {

  }

  private loadCurrentUser(): void {
    this.userService.getCurrentUser().subscribe({
      next: (user: UserResponse) => {
        this.currentUserId = user.id || null;
        console.log('Current user ID:', this.currentUserId);
      },
      error: (err) => {
        console.error('Error loading current user:', err);
        this.currentUserId = null;
      }
    });
  }

  private getSenderId(): number | undefined {
    if (this.selectedChat.senderId === this.currentUserId) {
      return this.selectedChat.senderId;
    }
    return this.selectedChat.recipientId;
  }

  private getRecipientId(): number | undefined {
    if (this.selectedChat.senderId === this.currentUserId) {
      return this.selectedChat.recipientId;
    }
    return this.selectedChat.senderId;
  }

  private getAllChatMessages(chatId: number) {
    this.messageService.getMessages({ 'chat-id': chatId }).subscribe({
      next: (msgs) => {
        this.chatMessages = msgs;
      },
      error: (err) => {
        console.error('Error loading messages:', err);
      },
    });
  }

  private setMessagesToSeen() {
    this.messageService.setMessagesToSeen({
      'chat-id': this.selectedChat.id? this.selectedChat.id : 0
    }).subscribe({
      next:() => {}
    });
  }

  private loadChats(): void {
    this.chatService.getAllChatsByRecipientId().subscribe({
      next: (chats) => {
        this.chats = chats;
        console.log('Loaded chats:', chats);
      },
      error: (err) => {
        console.error('Error loading chats:', err);
      },
    });
  }
}