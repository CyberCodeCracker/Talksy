package com.amouri_dev.talksy.infrastructure;

import com.amouri_dev.talksy.entities.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
}
