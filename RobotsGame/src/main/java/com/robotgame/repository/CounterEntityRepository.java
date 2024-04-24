package com.robotgame.repository;

import com.robotgame.domain.CounterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CounterEntityRepository extends JpaRepository<CounterEntity, Long> {
}
