package com.amouri_dev.talksy.infrastructure;

import com.amouri_dev.talksy.entities.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query(""" 
            select user
            FROM User user
            where user.email != :email
            """)
    Page<User> findAllUsersExceptSelf(String email, int page, int size);
    Optional<User> findUserById(Long id);
}
