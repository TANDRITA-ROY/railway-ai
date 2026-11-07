
package com.railmate.tool;

import com.railmate.dto.BookingDetailsResponse;
import com.railmate.service.BookingDetailsService;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;

import org.springframework.stereotype.Component;

@Component
public class BookingDetailsTool {

    private final BookingDetailsService bookingDetailsService;

    public BookingDetailsTool(
            BookingDetailsService bookingDetailsService
    ) {
        this.bookingDetailsService = bookingDetailsService;
    }

    @Tool("Get complete details of a railway booking using its booking ID, including PNR, ticket status, train, journey, fare and passenger details")
    public BookingDetailsResponse getBookingDetails(
            @P("The booking ID") Long bookingId
    ) {
        return bookingDetailsService.getBookingDetails(bookingId);
    }
}
