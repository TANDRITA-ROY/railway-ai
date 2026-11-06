package com.railmate.service;
import com.railmate.entity.Passenger;
import com.railmate.exception.ResourceNotFoundException;
import com.railmate.repository.PassengerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class PassengerService {
    private final PassengerRepository repository;

    public PassengerService(PassengerRepository repository) {
        this.repository = repository;
    }

    public Passenger create(Passenger value) {
        value.setId(null);
        return repository.save(value);
    }

    @Transactional(readOnly = true)
    public List<Passenger> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Passenger findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with id: " + id));
    }

    public Passenger update(Long id, Passenger value) {
        Passenger existing = findById(id);
        value.setId(existing.getId());
        return repository.save(value);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
