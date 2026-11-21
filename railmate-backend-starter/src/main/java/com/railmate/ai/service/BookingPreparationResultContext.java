
package com.railmate.ai.service;

import com.railmate.dto.BookingPreparationResponse;
import org.springframework.stereotype.Component;

@Component
public class BookingPreparationResultContext {

    private final ThreadLocal<BookingPreparationResponse> result =
            new ThreadLocal<>();

    public void setResult(BookingPreparationResponse booking) {
        result.set(booking);
    }

    public BookingPreparationResponse getResult() {
        return result.get();
    }

    public void clear() {
        result.remove();
    }
}
