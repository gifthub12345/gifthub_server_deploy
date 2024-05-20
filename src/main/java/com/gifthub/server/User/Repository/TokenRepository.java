package com.gifthub.server.User.Repository;

import com.gifthub.server.User.Jwt.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<Token, Long> {
}
