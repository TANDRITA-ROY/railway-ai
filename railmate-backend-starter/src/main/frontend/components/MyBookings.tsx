import { useCallback, useEffect, useState } from "react";
import CancellationDialog from "./CancellationDialog";
import TicketCard from "./TicketCard";
import { getMyBookings } from "../services/bookingService";
import type { BookingDetails } from "../types/BookingDetails";

export default function MyBookings() {
    const [bookings, setBookings] = useState<BookingDetails[]>([]);
    const [selected, setSelected] = useState<BookingDetails | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const loadBookings = useCallback(async () => {
        setLoading(true);
        setError("");
        try {
            setBookings(await getMyBookings());
        } catch (err) {
            setError(err instanceof Error ? err.message : "Unable to load bookings.");
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => { void loadBookings(); }, [loadBookings]);

    if (selected) {
        return <TicketCard booking={selected} onBack={() => setSelected(null)} />;
    }

    return (
        <div className="feature-page">
            <div className="feature-page-heading">
                <div><p className="eyebrow">YOUR TRAVEL</p><h1>My bookings</h1>
                    <p>View tickets and manage your reservations.</p></div>
                <button className="secondary-button" onClick={() => void loadBookings()}>Refresh</button>
            </div>
            {loading && <p>Loading your bookings...</p>}
            {error && <div className="feature-error">{error}</div>}
            {!loading && !error && bookings.length === 0 && (
                <section className="feature-card"><h2>No bookings yet</h2><p>Your confirmed bookings will appear here.</p></section>
            )}
            <div className="my-bookings-list">
                {bookings.map((booking) => (
                    <article className="feature-card my-booking-card" key={booking.bookingId}>
                        <div className="booking-card-heading">
                            <div><p className="eyebrow">PNR</p><h2>{booking.pnr}</h2></div>
                            <span className={booking.bookingStatus.toUpperCase() === "CONFIRMED" ? "feature-status" : "feature-status cancelled"}>
                                {booking.bookingStatus}
                            </span>
                        </div>
                        <strong>{booking.trainNumber} - {booking.trainName}</strong>
                        <p>{booking.sourceStationCode} ({booking.sourceStationName}) → {booking.destinationStationCode} ({booking.destinationStationName})</p>
                        <div className="booking-card-meta">
                            <span>Journey: {booking.journeyDate}</span>
                            <span>Passengers: {booking.passengers.length}</span>
                            <strong>₹{Number(booking.totalFare).toLocaleString("en-IN")}</strong>
                        </div>
                        <div className="feature-actions">
                            <button className="secondary-button" onClick={() => setSelected(booking)}>View ticket</button>
                        </div>
                        {booking.bookingStatus.toUpperCase() !== "CANCELLED" && (
                            <CancellationDialog
                                bookingId={booking.bookingId}
                                pnr={booking.pnr}
                                onCancelled={() => void loadBookings()}
                            />
                        )}
                    </article>
                ))}
            </div>
        </div>
    );
}
