package com.railmate.controller;
import com.railmate.entity.Station;
import com.railmate.service.StationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stations")
public class StationController {
    private final StationService service;

    public StationController(StationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Station> create(@Valid @RequestBody Station value) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(value));
    }

    @GetMapping
    public ResponseEntity<List<Station>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Station> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Station> update(@PathVariable Long id, @Valid @RequestBody Station value) {
        return ResponseEntity.ok(service.update(id, value));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
