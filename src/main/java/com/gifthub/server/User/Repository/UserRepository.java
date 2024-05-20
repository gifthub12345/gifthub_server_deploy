package com.gifthub.server.User.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gifthub.server.User.Entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByIdentifier(String identifier);
}
