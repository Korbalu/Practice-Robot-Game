package com.robotgame.repository;

import com.robotgame.domain.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomUserRepository extends JpaRepository<CustomUser, Long> {

    Optional<CustomUser> findByEmail(String email);
    @Query("select u from CustomUser u where u.email =:email")
    Optional<CustomUser> findByMail(@Param("email") String email);

}
