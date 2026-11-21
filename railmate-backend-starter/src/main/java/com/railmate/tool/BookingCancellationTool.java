
package com.railmate.tool;

import com.railmate.ai.service.CancellationPreparationResultContext;
import com.railmate.dto.BookingCancellationPreparationResponse;
import com.railmate.service.BookingCancellationService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class BookingCancellationTool {

    private final BookingCancellationService cancellationService;
    private final CancellationPreparationResultContext resultContext;

    public BookingCancellationTool(
            BookingCancellationService cancellationService,
            CancellationPreparationResultContext resultContext
    ) {
        this.cancellationService = cancellationService;
        this.resultContext = resultContext;
    }

    @Tool("Prepare cancellation of a user's railway booking. "
            + "This does not cancel the booking. It only creates a confirmation token "
            + "and returns cancellation details for the user to review.")
    public BookingCancellationPreparationResponse requestCancellation(
            @P("The booking ID") Long bookingId,
            @P("The user ID who owns the booking") Long userId
    ) {
        BookingCancellationPreparationResponse response =
                cancellationService.requestCancellation(bookingId, userId);

        resultContext.setResult(response);

        return response;
    }
}
