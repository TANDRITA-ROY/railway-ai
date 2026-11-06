package com.railmate.service;
import com.railmate.entity.TrainStop;
import com.railmate.exception.ResourceNotFoundException;
import com.railmate.repository.TrainStopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class TrainStopService {
    private final TrainStopRepository repository;

    public TrainStopService(TrainStopRepository repository) {
        this.repository = repository;
    }

    public TrainStop create(TrainStop value) {
        value.setId(null);
        return repository.save(value);
    }

    @Transactional(readOnly = true)
    public List<TrainStop> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public TrainStop findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("TrainStop not found with id: " + id));
    }

    public TrainStop update(Long id, TrainStop value) {
        TrainStop existing = findById(id);
        value.setId(existing.getId());
        return repository.save(value);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
