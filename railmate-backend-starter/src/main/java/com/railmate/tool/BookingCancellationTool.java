
package com.railmate.tool;

import com.railmate.service.BookingCancellationService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class BookingCancellationTool {

    private final BookingCancellationService cancellationService;

    public BookingCancellationTool(
            BookingCancellationService cancellationService) {
        this.cancellationService = cancellationService;
    }

    @Tool("Request cancellation of a user's railway booking. "
            + "This does not cancel the booking. It only creates a confirmation token.")
    public String requestCancellation(
            @P("The booking ID") Long bookingId,
            @P("The user ID who owns the booking") Long userId) {

        return cancellationService.requestCancellation(bookingId, userId);
    }
}
