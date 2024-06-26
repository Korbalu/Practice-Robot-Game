package com.robotgame.repository;

import com.robotgame.domain.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    @Query("select t from City t where t.owner.id=:id")
    Optional<City> findByOwner(@Param("id") Long id);

    @Query("select t from City t where t.owner.name=:name")
    Optional<City> findByOwnerName(@Param("name") String name);

    @Query("select c from City c order by c.score desc")
    List<City> findAllOrderByScore();

    @Query("select c from City c where c.name like 'AutoCity%'")
    List<City> findAllAutoCity();

}
