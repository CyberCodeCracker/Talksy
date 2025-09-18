package com.amouri_dev.talksy.chat;

import com.amouri_dev.talksy.common.BaseAuditingEntity;
import com.amouri_dev.talksy.message.Message;
import com.amouri_dev.talksy.message.MessageState;
import com.amouri_dev.talksy.message.MessageType;
import com.amouri_dev.talksy.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CHATS")
public class Chat extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "CHAT_ID", columnDefinition = "BIGSERIAL")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "SENDER_ID")
    private User sender;
    @ManyToOne
    @JoinColumn(name = "RECIPIENT_ID")
    private User recipient;
    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    @OrderBy("createdDate DESC")
    private List<Message> messages;

    @Transient
    public String getChatName(final String senderId) {
        if (recipient.getId().equals(senderId)) {
            return sender.getFirstName() + " " + sender.getLastName();
        }
        return recipient.getFirstName() + " " + recipient.getLastName();
    }

    @Transient
    public long getUnreadMessagesCount(final String senderId) {
        return messages.stream()
                .filter(message -> message.getReceiverId().equals(senderId))
                .filter(message -> message.getState() == MessageState.SENT)
                .count()
                ;
    }

    @Transient
    public String getLastMessage() {
        if(messages != null && !messages.isEmpty()) {
            if (messages.get(0).getType() != MessageType.TEXT) {
                return "Attachment";
            }
            return messages.get(0).getContent();
        }
        return null;
    }

    @Transient
    public LocalDateTime getLastMessageTime() {
        if(messages != null && !messages.isEmpty()) {
            return messages.get(0).getCreatedDate();
        }
        return null;
    }
}
