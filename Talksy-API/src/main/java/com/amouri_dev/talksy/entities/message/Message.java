package com.amouri_dev.talksy.entities.message;

import com.amouri_dev.talksy.entities.chat.Chat;
import com.amouri_dev.talksy.common.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MESSAGES")
public class Message extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mess_seq")
    @SequenceGenerator(name = "mess_seq", sequenceName = "mess_seq", allocationSize = 1)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Enumerated(EnumType.STRING)
    private MessageState state;
    @Enumerated(EnumType.STRING)
    private MessageType type;
    @ManyToOne
    @JoinColumn(name = "CHAT_ID")
    private Chat chat;
    @Column(name = "SENDER_ID", nullable = false)
    private String senderId;
    @Column(name = "RECEIVER_ID", nullable = false)
    private String receiverId;

}
