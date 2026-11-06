package com.railmate.service;
import com.railmate.entity.SeatInventory;
import com.railmate.exception.ResourceNotFoundException;
import com.railmate.repository.SeatInventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class SeatInventoryService {
    private final SeatInventoryRepository repository;

    public SeatInventoryService(SeatInventoryRepository repository) {
        this.repository = repository;
    }

    public SeatInventory create(SeatInventory value) {
        value.setId(null);
        return repository.save(value);
    }

    @Transactional(readOnly = true)
    public List<SeatInventory> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public SeatInventory findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("SeatInventory not found with id: " + id));
    }

    public SeatInventory update(Long id, SeatInventory value) {
        SeatInventory existing = findById(id);
        value.setId(existing.getId());
        return repository.save(value);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
