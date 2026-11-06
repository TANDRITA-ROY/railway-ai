package com.railmate.controller;
import com.railmate.entity.TrainStop;
import com.railmate.service.TrainStopService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/train-stops")
public class TrainStopController {
    private final TrainStopService service;

    public TrainStopController(TrainStopService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TrainStop> create(@Valid @RequestBody TrainStop value) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(value));
    }

    @GetMapping
    public ResponseEntity<List<TrainStop>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainStop> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainStop> update(@PathVariable Long id, @Valid @RequestBody TrainStop value) {
        return ResponseEntity.ok(service.update(id, value));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
