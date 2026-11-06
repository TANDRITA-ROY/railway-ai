package com.railmate.repository;
import com.railmate.entity.SeatInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatInventoryRepository extends JpaRepository<SeatInventory, Long> {
}
