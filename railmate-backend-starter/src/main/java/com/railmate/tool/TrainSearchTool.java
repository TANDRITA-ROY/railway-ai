
package com.railmate.tool;

import com.railmate.ai.service.TrainSearchResultContext;
import com.railmate.dto.TrainSearchResponse;
import com.railmate.service.TrainSearchService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainSearchTool {

    private final TrainSearchService trainSearchService;
    private final TrainSearchResultContext resultContext;

    public TrainSearchTool(
            TrainSearchService trainSearchService,
            TrainSearchResultContext resultContext
    ) {
        this.trainSearchService = trainSearchService;
        this.resultContext = resultContext;
    }

    @Tool("Search for trains running between two railway stations")
    public List<TrainSearchResponse> searchTrains(
            @P("Source station code or name") String source,
            @P("Destination station code or name") String destination
    ) {

        List<TrainSearchResponse> trains =
                trainSearchService.searchTrains(source, destination);

        // Capture the actual database results for the frontend
        resultContext.setResults(trains);

        return trains;
    }
}
