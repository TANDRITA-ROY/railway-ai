package com.railmate.controller;
import com.railmate.dto.BookingDetailsResponse;
import com.railmate.entity.Booking;
import com.railmate.service.BookingDetailsService;
import com.railmate.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService service;
    private final BookingDetailsService bookingDetailsService;

    public BookingController(BookingService service,BookingDetailsService bookingDetailsService) {
        this.service = service;
        this.bookingDetailsService=bookingDetailsService;
    }

    @PostMapping
    public ResponseEntity<Booking> create(@Valid @RequestBody Booking value) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(value));
    }

    @GetMapping
    public ResponseEntity<List<Booking>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDetailsResponse> findById(
            @PathVariable Long id
    ) {
        BookingDetailsResponse response =
                bookingDetailsService.getBookingDetails(id);

        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Booking> update(@PathVariable Long id, @Valid @RequestBody Booking value) {
        return ResponseEntity.ok(service.update(id, value));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
