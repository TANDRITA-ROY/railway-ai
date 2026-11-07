package com.railmate.dto;

public record PassengerDetailsResponse(
        String name,
        Integer age,
        String gender,
        String seatNumber
) {}