package com.amouri_dev.talksy.entities.group_chat;

import com.amouri_dev.talksy.common.BaseAuditingEntity;
import com.amouri_dev.talksy.entities.chat.Chat;
import com.amouri_dev.talksy.entities.message.Message;
import com.amouri_dev.talksy.entities.message.MessageState;
import com.amouri_dev.talksy.entities.message.MessageType;
import com.amouri_dev.talksy.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "GROUP_CHAT")
@SuperBuilder
public class GroupChat extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_chat_seq")
    @SequenceGenerator(name = "group_chat_seq", sequenceName = "group_chat_seq", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATOR_ID")
    private User creator;

    @ManyToMany
    @JoinTable(
            name = "GROUP_PARTICIPANTS",
            joinColumns = @JoinColumn(name = "GROUP_ID"),
            inverseJoinColumns = @JoinColumn(name = "PARTICIPANT_ID")
    )
    private Set<User> participants;

    @OneToMany(mappedBy = "groupChat", fetch = FetchType.EAGER)
    private List<Message> messages;

    @Column(name = "CHAT_NAME")
    private String chatName;

    public void addParticipant(User participant) {
        this.participants.add(participant);
    }


    @Transient
    public long getUnreadMessagesCount(final Long senderId) {
        return messages.stream()
                .filter(message -> message.getRecipientId().equals(senderId))
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
