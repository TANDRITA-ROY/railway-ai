export interface PassengerRequest {
    name: string;
    age: number;
    gender: string;
}

export interface BookingRequest {
    userId: number;
    trainNumber: string;
    source: string;
    destination: string;
    journeyDate: string;
    coachType: string;
    passengers: PassengerRequest[];
}

export interface BookingPreparation {
    confirmationToken: string;
    userId: number;
    trainNumber: string;
    trainName: string;
    source: string;
    destination: string;
    journeyDate: string;
    coachType: string;
    passengerCount: number;
    passengers: PassengerRequest[];
    estimatedTotalFare: number;
    message: string;
}

export interface BookingCreationResult {
    bookingId: number;
    pnr: string;
    bookingStatus: string;
    ticketStatus: string;
    trainNumber: string;
    trainName: string;
    source: string;
    destination: string;
    journeyDate: string;
    coachNumber: string;
    totalFare: number;
    passengers: Array<PassengerRequest & { seatNumber: string }>;
    message: string;
}
