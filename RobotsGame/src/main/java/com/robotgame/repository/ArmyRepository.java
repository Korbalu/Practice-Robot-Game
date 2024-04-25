package com.robotgame.repository;


import com.robotgame.domain.Legion;
import com.robotgame.domain.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ArmyRepository extends JpaRepository<Legion, Long> {
    @Query("select a from Legion a where a.owner.id =:id and a.type =:id2")
    Legion findByOwnerAndType(@Param("id") Long id, @Param("id2") Unit id2);
    @Query("select a from Legion a where a.owner.id =:id")
    List<Legion> findAllByOwner(@Param("id") Long id);
    @Query("select a from Legion a where a.owner.name =:name")
    List<Legion> findAllByOwnerName(@Param("name") String name);
    @Query("select SUM(l.quantity) from Legion l where l.owner.name =:name")
    Long findUnitQuantity(@Param("name") String name);

    @Query("select a from Legion a where a.owner.name =:name and a.type =:id")
    Legion findByOwnerNameAndType(@Param("name") String name, @Param("id") Unit id);
    @Modifying
    @Transactional
    @Query("delete from Legion l where l.quantity =:amount")
    void deleteAllByQuantity(@Param("amount") Long amount);

    @Query("select l from Legion l where l.type =:type")
    List<Legion> findALLByType(@Param("type") Unit type);

}
