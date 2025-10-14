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

  private chatService = inject(ChatService);
  private userService = inject(UserService);

  ngOnInit(): void {
    this.loadUsers();
  }

  chatClicked(chat: ChatResponse): void {
    this.chatSelected.emit(chat);
  }

  selectContact(contact: UserResponse) {

  }

  wrapMessage(message: string | undefined): string {
    if (message && message.length <= 20) {
      return message;
    }
    return message?.substring(0, 17) + '...';
  }

  private loadUsers(): void {
    this.userService.getUsers().subscribe({
      next: (users: UserResponse[]) => {
        this.contacts = users || [];
        console.log("loaded contacts", this.contacts);
      },
      error: (err) => {
        console.log("Error loading contacts ", err);
        this.contacts = [];
      }
    })
  }
}
