package com.railmate.controller;

import com.railmate.dto.TrainSearchResponse;
import com.railmate.service.TrainSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trains")
public class TrainSearchController {

    private final TrainSearchService trainSearchService;

    public TrainSearchController(TrainSearchService trainSearchService) {
        this.trainSearchService = trainSearchService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<TrainSearchResponse>> searchTrains(
            @RequestParam String source,
            @RequestParam String destination
    ) {

        List<TrainSearchResponse> results =
                trainSearchService.searchTrains(source, destination);

        return ResponseEntity.ok(results);
    }
}