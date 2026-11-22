
export interface CancellationPreparation {
    confirmationToken: string;
    bookingId: number;
    pnr: string;
    message: string;
}

export interface CancellationResult {
    bookingId: number;
    pnr: string;
    bookingStatus: string;
    ticketStatus: string;
    message: string;
}
