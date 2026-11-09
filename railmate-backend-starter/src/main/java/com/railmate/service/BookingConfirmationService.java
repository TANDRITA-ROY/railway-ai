package com.railmate.service;

import com.railmate.dto.BookingCreationRequest;
import com.railmate.dto.BookingCreationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookingConfirmationService {

    private final BookingConfirmationStore confirmationStore;
    private final BookingCreationService bookingCreationService;

    public BookingConfirmationService(
            BookingConfirmationStore confirmationStore,
            BookingCreationService bookingCreationService
    ) {
        this.confirmationStore = confirmationStore;
        this.bookingCreationService = bookingCreationService;
    }

    public BookingCreationResponse confirmBooking(String confirmationToken) {

        BookingCreationRequest request = confirmationStore
                .consume(confirmationToken)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid, expired, or already-used confirmation token."
                ));

        return bookingCreationService.createBooking(request);
    }
}