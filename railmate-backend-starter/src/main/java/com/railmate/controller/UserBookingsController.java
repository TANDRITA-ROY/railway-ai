package com.railmate.controller;

import com.railmate.dto.UserBookingResponse;
import com.railmate.service.UserBookingsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserBookingsController {

    private final UserBookingsService userBookingsService;

    public UserBookingsController(UserBookingsService userBookingsService) {
        this.userBookingsService = userBookingsService;
    }

    @GetMapping("/{userId}/bookings")
    public List<UserBookingResponse> getUserBookings(
            @PathVariable Long userId) {

        return userBookingsService.getUserBookings(userId);
    }
}