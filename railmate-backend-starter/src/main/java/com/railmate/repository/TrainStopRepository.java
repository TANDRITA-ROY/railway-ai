package com.railmate.repository;
import com.railmate.entity.TrainStop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainStopRepository extends JpaRepository<TrainStop, Long> {
}
