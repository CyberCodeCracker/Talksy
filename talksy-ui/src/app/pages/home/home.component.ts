import {
  AfterViewChecked,
  Component,
  DestroyRef,
  ElementRef,
  inject,
  OnDestroy,
  OnInit,
  signal,
  ViewChild,
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
import { Subscription } from 'rxjs';

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
export class HomeComponent implements OnInit, OnDestroy, AfterViewChecked {
  chats: Array<ChatResponse> = [];
  selectedChat: ChatResponse = {};
  chatMessages: Array<MessageResponse> = [];
  currentUserId: number | null = null;
  showEmojis: boolean = false;
  messageContent: string = '';
  recipientId = this.getSenderId();
  socketClient: any = null;
  @ViewChild('scrollableDiv') scrollableDiv!: ElementRef<HTMLDivElement>;

  private notificationSubscription: any;
  private destroyRef = inject(DestroyRef);
  private chatService = inject(ChatService);
  private messageService = inject(MessageService);
  private tokenService = inject(TokenService);
  private userService = inject(UserService);
  private router = inject(Router);

  searchNewContact = signal(false);
  contactSearch = signal<string>('');
  chatFilter = signal<'all' | 'unread'>('all');

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadChats();
  }

  ngAfterViewChecked(): void {
    this.scrollBottom();
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

  updateSearchText(text: string) {
    this.contactSearch.set(text);
  }

  setChatFilter(filter: 'all' | 'unread') {
    this.chatFilter.set(filter);
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
    this.selectedChat.unreadChatsCount = 0;
    console.log('Is recipient online: ' + this.selectedChat.recipientOnline);
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

    this.messageContent += (emoji as any).native ?? emoji.name;
  }

  keyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      this.sendMessage();
    }
  }

  uploadMedia(target: EventTarget | null): void {
    const file = this.extractFileFromTarget(target);
    if (file !== null) {
      const reader = new FileReader();
      reader.onload = () => {
        if (reader.result) {
          const mediaLines = reader.result.toString().split(',')[1];
          if (!this.selectedChat.id) return;
          const subscription = this.messageService
            .uploadMedia({
              'chat-id': this.selectedChat.id,
              body: {
                file: file,
              },
            })
            .subscribe({
              next: () => {
                const message: MessageResponse = {
                  senderId: this.getSenderId(),
                  recipientId: this.getRecipientId(),
                  message: 'Attachment',
                  type: 'IMAGE',
                  state: 'SENT',
                  media: [mediaLines],
                  createdAt: new Date().toString(),
                };
                this.chatMessages.push(message);
              },
            });
          this.destroyRef.onDestroy(() => {
            subscription.unsubscribe();
          });
        }
      };
      reader.readAsDataURL(file);
    }
  }

  filteredChats(): Array<ChatResponse> {
    const filter = this.chatFilter();
    if (filter === 'unread') {
      return (this.chats || []).filter((c) => (c.unreadChatsCount ?? 0) > 0);
    }
    return this.chats || [];
  }

  private extractFileFromTarget(target: EventTarget | null): File | null {
    const htmlInputTarget = target as HTMLInputElement;
    if (target === null || htmlInputTarget.files === null) {
      return null;
    }
    return htmlInputTarget.files[0];
  }

  private handleNotification(notification: Notification) {
    console.log(
      `🔔 Notification [${notification.type}] for chat ${notification.chatId}:`,
      notification
    );

    if (!notification) return;

    if (this.selectedChat && this.selectedChat.id === notification.chatId) {
      switch (notification.type) {
        case 'MESSAGE':
        case 'IMAGE':
          const content = (notification as any).content ?? notification.message;
          console.log('📱 Adding message to UI:', content);
          const message: MessageResponse = {
            senderId: notification.senderId,
            recipientId: notification.recipientId,
            message: content,
            type: notification.messageType,
            media: notification.media,
            createdAt: new Date().toString(),
          };
          this.chatMessages.push(message);

          this.selectedChat.lastMessage = content || 'Attachment';
          break;

        case 'SEEN':
          console.log('👁️ Marking messages as SEEN');
          this.chatMessages.forEach((m) => (m.state = 'SEEN'));
          break;
      }
    } else {
      const destChat = this.chats.find(
        (chat) => chat.id === notification.chatId
      );
      if (destChat && notification.type !== 'SEEN') {
        const content = (notification as any).content ?? notification.message;
        destChat.lastMessage = content || 'Attachment';
        destChat.lastMessageTime = new Date().toString();
        destChat.unreadChatsCount = (destChat.unreadChatsCount ?? 0) + 1;
      }
    }
  }

  private loadCurrentUser(): void {
    const subscription = this.userService.getCurrentUser().subscribe({
      next: (user: UserResponse) => {
        this.currentUserId = user.id || null;
        console.log('Current user ID:', this.currentUserId);
        if (this.currentUserId) {
          this.initWebSocket();
        } else {
          console.error('Cannot initialize WebSocket: currentUserId is null');
        }
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

  private initWebSocket(): void {
    if (!this.currentUserId) {
      console.error('Cannot initialize WebSocket: currentUserId is null');
      return;
    }

    let ws = new SockJs('http://localhost:8080/websocket');
    this.socketClient = Stomp.over(ws);

    const subUrl = `/user/${this.currentUserId}/chat`;
    console.log('Connecting to WebSocket:', subUrl);

    this.socketClient.connect(
      {
        Authorization: 'Bearer ' + this.tokenService.getAccessToken(),
      },
      () => {
        console.log('WebSocket connected successfully');
        this.notificationSubscription = this.socketClient.subscribe(
          subUrl,
          (message: any) => {
            const notification: Notification = JSON.parse(message.body);
            console.log('Received notification:', notification);
            this.handleNotification(notification);
          },
          (error: any) => {
            console.error('Error in WebSocket subscription:', error);
          }
        );
      },
      (error: any) => {
        console.error('Error while connecting to WebSocket:', error);
      }
    );
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
    this.destroySubscription(subscription);
  }

  private destroySubscription(subscription: Subscription) {
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
    this.destroySubscription(subscription);
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
    this.destroySubscription(subscription);
  }

  private scrollBottom() {
    if (this.scrollableDiv) {
      const div = this.scrollableDiv.nativeElement;
      div.scrollTop = div.scrollHeight;
    }
  }
}
