package com.railmate.controller;

import com.railmate.dto.BookingCreationRequest;
import com.railmate.dto.BookingCreationResponse;
import com.railmate.dto.BookingPreparationResponse;
import com.railmate.service.BookingCreationService;
import com.railmate.service.BookingPreparationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingCreationController {

    private final BookingCreationService bookingCreationService;
    private final BookingPreparationService bookingPreparationService;

    public BookingCreationController(
            BookingCreationService bookingCreationService,
            BookingPreparationService bookingPreparationService
    ) {
        this.bookingCreationService = bookingCreationService;
        this.bookingPreparationService = bookingPreparationService;
    }

    // New endpoint for the frontend's review-before-confirm flow.
    @PostMapping("/prepare")
    public BookingPreparationResponse prepareBooking(
            @Valid @RequestBody BookingCreationRequest request
    ) {
        return bookingPreparationService.prepareBooking(request);
    }

    // Existing endpoint retained for compatibility.
    @PostMapping("/create")
    public BookingCreationResponse createBooking(
            @Valid @RequestBody BookingCreationRequest request
    ) {
        return bookingCreationService.createBooking(request);
    }
}
