package com.railmate.service;
import com.railmate.entity.Train;
import com.railmate.exception.ResourceNotFoundException;
import com.railmate.repository.TrainRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class TrainService {
    private final TrainRepository repository;

    public TrainService(TrainRepository repository) {
        this.repository = repository;
    }

    public Train create(Train value) {
        value.setId(null);
        return repository.save(value);
    }

    @Transactional(readOnly = true)
    public List<Train> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Train findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + id));
    }

    public Train update(Long id, Train value) {
        Train existing = findById(id);
        value.setId(existing.getId());
        return repository.save(value);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
