export interface Notification {
  chatId?: number,
  message?: string,
  senderId?: number,
  recipientId?: number,
  messageType: 'TEXT' | 'IMAGE' | 'VIDEO' | 'AUDIO';
  type?: 'SEEN' | 'MESSAGE' | 'IMAGE' | 'AUDIO' | 'VIDEO';
  chatName?: string,
  media?: string;
}