package com.gussbom.auth.repositories;

import com.gussbom.auth.entities.Token;
import com.gussbom.auth.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("""
    select t from Token t inner join AppUser u on t.user.id = u.id
    where u.id = :userid and (t.expired = false) and (t.type = :type)
          """)
    List<Token> findAllValidTokenByUserAndType(@Param("userid")Long userid, TokenType type);

    @Query("""
    select t from Token t inner join AppUser u on t.user.id = u.id
    where u.id = :userid and (t.expired = false)
          """)
    List<Token> findAllValidTokenByUser(@Param("userid")Long userid);

    Optional<Token> findByToken(String token);
}
