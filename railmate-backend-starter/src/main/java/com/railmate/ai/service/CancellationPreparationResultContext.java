
package com.railmate.ai.service;

import com.railmate.dto.BookingCancellationPreparationResponse;
import org.springframework.stereotype.Component;

@Component
public class CancellationPreparationResultContext {

    private final ThreadLocal<BookingCancellationPreparationResponse> result =
            new ThreadLocal<>();

    public void setResult(BookingCancellationPreparationResponse response) {
        result.set(response);
    }

    public BookingCancellationPreparationResponse getResult() {
        return result.get();
    }

    public void clear() {
        result.remove();
    }
}
