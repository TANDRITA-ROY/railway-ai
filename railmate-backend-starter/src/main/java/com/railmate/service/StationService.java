package com.railmate.service;
import com.railmate.entity.Station;
import com.railmate.exception.ResourceNotFoundException;
import com.railmate.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class StationService {
    private final StationRepository repository;

    public StationService(StationRepository repository) {
        this.repository = repository;
    }

    public Station create(Station value) {
        value.setId(null);
        return repository.save(value);
    }

    @Transactional(readOnly = true)
    public List<Station> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Station findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Station not found with id: " + id));
    }

    public Station update(Long id, Station value) {
        Station existing = findById(id);
        value.setId(existing.getId());
        return repository.save(value);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
