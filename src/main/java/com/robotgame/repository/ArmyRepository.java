package com.robotgame.repository;

import com.robotgame.domain.CustomUser;
import com.robotgame.domain.Legion;
import com.robotgame.domain.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArmyRepository extends JpaRepository<Legion, Long> {
    @Query("select a from Legion a where a.owner.id =:id and a.type =:id2")
    Legion findByOwnerAndType(@Param("id") Long id, @Param("id2") Unit id2);
}
