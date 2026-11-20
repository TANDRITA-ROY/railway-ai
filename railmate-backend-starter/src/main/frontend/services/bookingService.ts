import { CURRENT_USER_ID } from "../config";
import type { BookingDetails } from "../types/BookingDetails";
import type {
    BookingCreationResult,
    BookingPreparation,
    BookingRequest,
} from "../types/BookingRequest";

async function readError(response: Response): Promise<string> {
    const body = await response.text();
    if (!body) return `Request failed (${response.status})`;
    try {
        const parsed = JSON.parse(body);
        return parsed.message ?? parsed.detail ?? body;
    } catch {
        return body;
    }
}

export async function prepareBooking(
    request: Omit<BookingRequest, "userId">
): Promise<BookingPreparation> {
    const response = await fetch("/api/bookings/prepare", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ ...request, userId: CURRENT_USER_ID }),
    });
    if (!response.ok) throw new Error(await readError(response));
    return response.json();
}

export async function confirmBooking(
    confirmationToken: string
): Promise<BookingCreationResult> {
    const response = await fetch("/api/bookings/confirm", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ confirmationToken }),
    });
    if (!response.ok) throw new Error(await readError(response));
    return response.json();
}

export async function getBookingDetails(bookingId: number): Promise<BookingDetails> {
    const response = await fetch(`/api/bookings/${bookingId}`);
    if (!response.ok) throw new Error(await readError(response));
    return response.json();
}

interface UserBookingSummary {
    bookingId: number;
}

export async function getMyBookings(): Promise<BookingDetails[]> {
    const response = await fetch(`/api/users/${CURRENT_USER_ID}/bookings`);
    if (!response.ok) throw new Error(await readError(response));

    const summaries: UserBookingSummary[] = await response.json();
    return Promise.all(summaries.map((item) => getBookingDetails(item.bookingId)));
}

export async function requestCancellation(bookingId: number): Promise<string> {
    const response = await fetch(
        `/api/bookings/${bookingId}/cancellation-request?userId=${CURRENT_USER_ID}`,
        { method: "POST" }
    );
    if (!response.ok) throw new Error(await readError(response));
    return response.text();
}

export interface CancellationResult {
    bookingId: number;
    pnr: string;
    bookingStatus: string;
    ticketStatus: string;
    message: string;
}

export async function confirmCancellation(
    confirmationToken: string
): Promise<CancellationResult> {
    const response = await fetch("/api/bookings/cancel", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ confirmationToken }),
    });
    if (!response.ok) throw new Error(await readError(response));
    return response.json();
}
