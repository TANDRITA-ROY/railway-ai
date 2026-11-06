package com.railmate.controller;
import com.railmate.entity.SeatInventory;
import com.railmate.service.SeatInventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/seat-inventory")
public class SeatInventoryController {
    private final SeatInventoryService service;

    public SeatInventoryController(SeatInventoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SeatInventory> create(@Valid @RequestBody SeatInventory value) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(value));
    }

    @GetMapping
    public ResponseEntity<List<SeatInventory>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatInventory> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeatInventory> update(@PathVariable Long id, @Valid @RequestBody SeatInventory value) {
        return ResponseEntity.ok(service.update(id, value));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
