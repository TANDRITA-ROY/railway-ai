
export interface SeatInventoryDetails {
    id: number;

    train: {
        id: number;
        trainNumber: string;
        name: string;
        active: boolean | null;
    };

    coach: {
        id: number;
        coachNumber: string;
        coachType: string;
        totalSeats: number;
    };

    fromStation: {
        id: number;
        code: string;
        name: string;
        city: string;
        state: string;
    };

    toStation: {
        id: number;
        code: string;
        name: string;
        city: string;
        state: string;
    };

    journeyDate: string;
    availableSeats: number;
    fare: number;
}
