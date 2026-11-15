
package com.railmate.ai.service;

import com.railmate.dto.TrainSearchResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainSearchResultContext {

    private final ThreadLocal<List<TrainSearchResponse>> results =
            ThreadLocal.withInitial(List::of);

    public void setResults(List<TrainSearchResponse> trains) {
        results.set(List.copyOf(trains));
    }

    public List<TrainSearchResponse> getResults() {
        return results.get();
    }

    public void clear() {
        results.remove();
    }
}
