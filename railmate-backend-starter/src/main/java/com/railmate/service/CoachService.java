package com.railmate.service;
import com.railmate.entity.Coach;
import com.railmate.exception.ResourceNotFoundException;
import com.railmate.repository.CoachRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CoachService {
    private final CoachRepository repository;

    public CoachService(CoachRepository repository) {
        this.repository = repository;
    }

    public Coach create(Coach value) {
        value.setId(null);
        return repository.save(value);
    }

    @Transactional(readOnly = true)
    public List<Coach> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Coach findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Coach not found with id: " + id));
    }

    public Coach update(Long id, Coach value) {
        Coach existing = findById(id);
        value.setId(existing.getId());
        return repository.save(value);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
