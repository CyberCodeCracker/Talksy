import { Component, inject, input, OnInit, signal } from '@angular/core';
import { ChatListComponent } from '../../components/chat-list/chat-list.component';
import { ChatResponse } from '../../services/models';
import { ChatService } from '../../services/services';
import { ContactListComponent } from '../../components/contact-list/contact-list.component';
import { TokenService } from '../../../token.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [ChatListComponent, ContactListComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit {
  chats: Array<ChatResponse> = [];

  private chatService = inject(ChatService);
  private tokenService = inject(TokenService);
  private router = inject(Router);

  searchNewContact = signal(false);

  updateSearchContact(newValue: boolean) {
    console.log(newValue);
    this.searchNewContact.set(newValue);
  }

  ngOnInit(): void {
    this.loadChats();
  }

  private loadChats(): void {
    this.chatService.getAllChatsByRecipientId().subscribe({
      next: (chats) => {
        this.chats = chats;
      },
      error: (err) => {
        console.error('Error loading chats:', err);
      },
    });
  }

  userProfile() {

  }

  logout() {
    this.tokenService.clearTokens();
    this.router.navigate(['login']); 
  }
}
