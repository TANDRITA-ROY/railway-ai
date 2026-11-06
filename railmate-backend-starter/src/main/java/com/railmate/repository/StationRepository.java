package com.railmate.repository;
import com.railmate.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long> {
    Optional<Station> findByCodeIgnoreCase(String code);

    List<Station> findByNameContainingIgnoreCase(String name);
}
