package com.railmate.tool;

import com.railmate.dto.UserBookingResponse;
import com.railmate.service.UserBookingsService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserBookingsTool {

    private final UserBookingsService userBookingsService;

    public UserBookingsTool(UserBookingsService userBookingsService) {
        this.userBookingsService = userBookingsService;
    }

    @Tool("Get all railway bookings belonging to a user using their user ID")
    public List<UserBookingResponse> getUserBookings(
            @P("The ID of the user whose bookings should be retrieved") Long userId
    ) {
        return userBookingsService.getUserBookings(userId);
    }
}