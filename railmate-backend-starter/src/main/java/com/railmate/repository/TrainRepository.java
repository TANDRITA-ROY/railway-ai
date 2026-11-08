package com.railmate.repository;

import com.railmate.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainRepository extends JpaRepository<Train, Long> {

    Optional<Train> findByTrainNumberIgnoreCaseAndActiveTrue(String trainNumber);
}