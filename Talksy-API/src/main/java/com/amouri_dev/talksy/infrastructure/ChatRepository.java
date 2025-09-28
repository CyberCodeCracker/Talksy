package com.amouri_dev.talksy.infrastructure;

import com.amouri_dev.talksy.entities.chat.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;



public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("""
        SELECT DISTINCT c FROM Chat c
        WHERE c.sender.id = :senderId OR c.recipient.id = :senderId
        ORDER BY c.createdDate DESC
        """)
    Page<Chat> findChatsBySenderId(Long senderId, Pageable pageable);

    @Query("""
        SELECT DISTINCT chat FROM Chat chat
        WHERE (chat.sender.id = :senderId AND chat.recipient.id = :recipientId)
        OR (chat.sender.id = :recipientId AND chat.recipient.id = :senderId)
        """)
    Page<Chat> findChatsBetweenUsers(Long senderId, Long recipientId, Pageable pageable);
}

