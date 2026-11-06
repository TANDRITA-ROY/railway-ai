package com.railmate.service;
import com.railmate.entity.Booking;
import com.railmate.exception.ResourceNotFoundException;
import com.railmate.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class BookingService {
    private final BookingRepository repository;

    public BookingService(BookingRepository repository) {
        this.repository = repository;
    }

    public Booking create(Booking value) {
        value.setId(null);
        return repository.save(value);
    }

    @Transactional(readOnly = true)
    public List<Booking> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Booking findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
    }

    public Booking update(Long id, Booking value) {
        Booking existing = findById(id);
        value.setId(existing.getId());
        return repository.save(value);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
