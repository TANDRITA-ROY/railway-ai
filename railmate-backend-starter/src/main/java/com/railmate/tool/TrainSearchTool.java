package com.railmate.tool;

import com.railmate.dto.TrainSearchResponse;
import com.railmate.service.TrainSearchService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainSearchTool {

    private final TrainSearchService trainSearchService;

    public TrainSearchTool(TrainSearchService trainSearchService) {
        this.trainSearchService = trainSearchService;
    }

    @Tool("Search for trains running between two railway stations")
    public List<TrainSearchResponse> searchTrains(
            @P("Source station code or name") String source,
            @P("Destination station code or name") String destination
    ) {
        return trainSearchService.searchTrains(source, destination);
    }
}