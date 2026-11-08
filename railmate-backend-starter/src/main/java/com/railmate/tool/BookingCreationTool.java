
package com.railmate.tool;

import com.railmate.dto.BookingCreationRequest;
import com.railmate.dto.BookingCreationResponse;
import com.railmate.service.BookingCreationService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class BookingCreationTool {

    private final BookingCreationService bookingCreationService;

    public BookingCreationTool(
            BookingCreationService bookingCreationService
    ) {
        this.bookingCreationService = bookingCreationService;
    }

    @Tool("Create a railway booking after the user has explicitly confirmed the complete booking details")
    public BookingCreationResponse createBooking(
            BookingCreationRequest request
    ) {
        return bookingCreationService.createBooking(request);
    }
}
