import {
  Component,
  DestroyRef,
  inject,
  input,
  OnDestroy,
  OnInit,
  signal,
} from '@angular/core';
import { ChatListComponent } from '../../components/chat-list/chat-list.component';
import {
  ChatResponse,
  MessageRequest,
  MessageResponse,
  UserResponse,
} from '../../services/models';
import {
  ChatService,
  MessageService,
  UserService,
} from '../../services/services';
import { ContactListComponent } from '../../components/contact-list/contact-list.component';
import { TokenService } from '../../services/token.service';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { PickerComponent } from '@ctrl/ngx-emoji-mart';
import { FormsModule } from '@angular/forms';
import { EmojiData } from '@ctrl/ngx-emoji-mart/ngx-emoji';
import * as Stomp from 'stompjs';
import SockJs from 'sockjs-client';
import { Notification } from './Notification';

@Component({
  selector: 'app-home',
  imports: [
    ChatListComponent,
    ContactListComponent,
    DatePipe,
    PickerComponent,
    FormsModule,
  ],
  standalone: true,
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit, OnDestroy {
  chats: Array<ChatResponse> = [];
  selectedChat: ChatResponse = {};
  chatMessages: Array<MessageResponse> = [];
  currentUserId: number | null = null;
  showEmojis: boolean = false;
  messageContent: string = '';
  recipientId = this.getSenderId();
  socketClient: any = null;
  private notificationSubscription: any;

  private destroyRef = inject(DestroyRef);
  private chatService = inject(ChatService);
  private messageService = inject(MessageService);
  private tokenService = inject(TokenService);
  private userService = inject(UserService);
  private router = inject(Router);

  searchNewContact = signal(false);

  ngOnInit(): void {
    this.loadCurrentUser();
    this.initWebSocket();
    this.loadChats();
  }

  ngOnDestroy(): void {
    if (this.socketClient !== null) {
      this.socketClient.disconnect();
      this.notificationSubscription.unsubscribe();
      this.socketClient = null;
    }
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
        messageType: 'TEXT',
      };
      const subscription = this.messageService
        .saveMessage({
          body: messageRequest,
        })
        .subscribe({
          next: () => {
            const message: MessageResponse = {
              senderId: this.getSenderId(),
              recipientId: this.getRecipientId(),
              message: this.messageContent,
              type: 'TEXT',
              state: 'SENT',
              createdAt: new Date().toString(),
            };
            this.selectedChat.lastMessage = this.messageContent;
            this.chatMessages.push(message);
            this.messageContent = '';
            this.showEmojis = false;
          },
        });
      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      });
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

  uploadMedia(target: EventTarget | null): void {}

  private initWebSocket() {
    let ws = new SockJs('http://localhost:8080/ws');
    this.socketClient = Stomp.over(ws);
    const subUrl = `/user/${this.currentUserId}/chat`;
    this.socketClient.connect(
      {
        Authorization: 'Bearer ' + this.tokenService.getAccessToken(),
      },
      () => {
        this.notificationSubscription = this.socketClient.subscribe(
          subUrl,
          (message: any) => {
            const notification: Notification = JSON.parse(message.body);

            this.handleNotification(notification);
          },
          () => {
            console.log('Error while connecting to websocket');
          }
        );
      }
    );
  }

  private handleNotification(notification: Notification) {
    if (!notification) return;
    if (this.selectedChat && this.selectedChat.id === notification.chatId) {
      switch (notification.type) {
        case 'MESSAGE':
        case 'IMAGE':
          const message: MessageResponse = {
            senderId: notification.senderId,
            recipientId: notification.recipientId,
            message: notification.message,
            type: notification.messageType,
            media: notification.media,
            createdAt: new Date().toString(),
          };
          if (notification.type === 'IMAGE') {
            this.selectedChat.lastMessage = 'Attachment';
          } else {
            this.selectedChat.lastMessage = notification.message;
          }
          this.chatMessages.push(message);
          break;
        case 'SEEN':
          this.chatMessages.forEach((m) => (m.state = 'SEEN'));
          break;
      }
    } else {
      const destChat = this.chats.find(
        (chat) => chat.id === notification.chatId
      );
      if (destChat && notification.type !== 'SEEN') {
        if (notification.type === 'MESSAGE') {
          destChat.lastMessage = notification.message;
        } else if (notification.type === 'IMAGE') {
          destChat.lastMessage = 'Attachment';
        }
        destChat.lastMessageTime = new Date().toString();
        destChat.unreadChatsCount! += 1;
      } else if (notification.type === 'MESSAGE') {
        const newChat: ChatResponse = {
          id: notification.chatId,
          senderId: notification.senderId,
          recipientId: notification.recipientId,
          lastMessage: notification.message,
          name: notification.chatName,
          unreadChatsCount: 1,
          lastMessageTime: new Date().toString(),
        };
        this.chats.unshift(newChat);
      }
    }
  }

  private loadCurrentUser(): void {
    const subscription = this.userService.getCurrentUser().subscribe({
      next: (user: UserResponse) => {
        this.currentUserId = user.id || null;
        console.log('Current user ID:', this.currentUserId);
      },
      error: (err) => {
        console.error('Error loading current user:', err);
        this.currentUserId = null;
      },
    });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
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
    const subscription = this.messageService
      .getMessages({ 'chat-id': chatId })
      .subscribe({
        next: (msgs) => {
          this.chatMessages = msgs;
        },
        error: (err) => {
          console.error('Error loading messages:', err);
        },
      });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }

  private setMessagesToSeen() {
    const subscription = this.messageService
      .setMessagesToSeen({
        'chat-id': this.selectedChat.id ? this.selectedChat.id : 0,
      })
      .subscribe({
        next: () => {},
      });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }

  private loadChats(): void {
    const subscription = this.chatService.getAllChatsByRecipientId().subscribe({
      next: (chats) => {
        this.chats = chats;
        console.log('Loaded chats:', chats);
      },
      error: (err) => {
        console.error('Error loading chats:', err);
      },
    });
        this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }
}
