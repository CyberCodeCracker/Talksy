package com.amouri_dev.talksy.infrastructure;

import com.amouri_dev.talksy.entities.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("""
        SELECT DISTINCT c FROM Chat c
        WHERE c.sender.id = :senderId OR c.recipient.id = :senderId
        ORDER BY c.createdDate DESC
        """)
    List<Chat> findChatsBySenderId(Long senderId);

    @Query("""
        SELECT DISTINCT chat FROM Chat chat
        WHERE (chat.sender.id = :senderId AND chat.recipient.id = :recipientId)
        OR (chat.sender.id = :recipientId AND chat.recipient.id = :senderId)
        """)
    Optional<Chat> findChatBetweenUsers(Long senderId, Long recipientId);

}

