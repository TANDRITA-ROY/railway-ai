
import type { PassengerDetails } from "./PassengerDetails";

export interface BookingDetails {
    bookingId: number;
    pnr: string;
    ticketStatus: string;
    issuedAt: string;

    trainNumber: string;
    trainName: string;

    sourceStationCode: string;
    sourceStationName: string;

    destinationStationCode: string;
    destinationStationName: string;

    journeyDate: string;
    bookingStatus: string;
    totalFare: number;

    passengers: PassengerDetails[];
}
