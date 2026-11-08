package com.railmate.controller;

import com.railmate.dto.BookingCreationRequest;
import com.railmate.dto.BookingCreationResponse;
import com.railmate.service.BookingCreationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingCreationController {

    private final BookingCreationService bookingCreationService;

    public BookingCreationController(
            BookingCreationService bookingCreationService) {
        this.bookingCreationService = bookingCreationService;
    }

    @PostMapping("/create")
    public BookingCreationResponse createBooking(
            @Valid @RequestBody BookingCreationRequest request) {

        return bookingCreationService.createBooking(request);
    }
}