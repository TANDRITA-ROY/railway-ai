package com.railmate.controller;

import com.railmate.dto.BookingConfirmationRequest;
import com.railmate.dto.BookingCreationResponse;
import com.railmate.service.BookingConfirmationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingConfirmationController {

    private final BookingConfirmationService confirmationService;

    public BookingConfirmationController(
            BookingConfirmationService confirmationService
    ) {
        this.confirmationService = confirmationService;
    }

    @PostMapping("/confirm")
    public BookingCreationResponse confirmBooking(
            @Valid @RequestBody BookingConfirmationRequest request
    ) {
        return confirmationService.confirmBooking(
                request.confirmationToken()
        );
    }
}