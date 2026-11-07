package com.railmate.controller;

import com.railmate.dto.BookingCancellationResponse;
import com.railmate.service.BookingCancellationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingCancellationController {

    private final BookingCancellationService cancellationService;

    public BookingCancellationController(
            BookingCancellationService cancellationService) {
        this.cancellationService = cancellationService;
    }

    @PostMapping("/{bookingId}/cancellation-request")
    public String requestCancellation(
            @PathVariable Long bookingId,
            @RequestParam Long userId) {

        return cancellationService.requestCancellation(
                bookingId, userId
        );
    }

    @PostMapping("/{bookingId}/cancel")
    public BookingCancellationResponse confirmCancellation(
            @PathVariable Long bookingId,
            @RequestParam Long userId,
            @RequestParam String confirmationToken) {

        return cancellationService.confirmCancellation(
                bookingId, userId, confirmationToken
        );
    }
}