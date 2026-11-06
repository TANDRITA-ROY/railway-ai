package com.railmate.service;
import com.railmate.entity.Ticket;
import com.railmate.exception.ResourceNotFoundException;
import com.railmate.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class TicketService {
    private final TicketRepository repository;

    public TicketService(TicketRepository repository) {
        this.repository = repository;
    }

    public Ticket create(Ticket value) {
        value.setId(null);
        return repository.save(value);
    }

    @Transactional(readOnly = true)
    public List<Ticket> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Ticket findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
    }

    public Ticket update(Long id, Ticket value) {
        Ticket existing = findById(id);
        value.setId(existing.getId());
        return repository.save(value);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
