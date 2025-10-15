package com.amouri_dev.talksy.infrastructure;

import com.amouri_dev.talksy.entities.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("""
        SELECT DISTINCT c FROM Chat c
        WHERE c.sender.id = :senderId OR c.recipient.id = :senderId
        ORDER BY c.createdDate DESC
        """)
    List<Chat> findChatsBySenderId(Long senderId);

    @Query("SELECT c FROM Chat c WHERE " +
            "(c.sender.id = :user1 AND c.recipient.id = :user2) OR " +
            "(c.sender.id = :user2 AND c.recipient.id = :user1)")
    Optional<Chat> findChatBetweenUsers(@Param("user1") Long user1, @Param("user2") Long user2);

}

