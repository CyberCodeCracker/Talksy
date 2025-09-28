package com.amouri_dev.talksy.infrastructure;

import com.amouri_dev.talksy.entities.message.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("""
            SELECT messages
            FROM Message messages
            WHERE messages.chat.id = :chatId
            ORDER BY messages.createdDate
            """)
    Page<Message> getMessagesByChatId(Long id, Pageable pageable);
    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE Message
            SET state = :newState
            WHERE chat.id = :chatId
            """)
    void updateState(Long chatId, String newState);

}
