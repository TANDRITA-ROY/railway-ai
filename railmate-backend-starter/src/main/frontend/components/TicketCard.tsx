import type { BookingDetails } from "../types/BookingDetails";

interface Props {
    booking: BookingDetails;
    onBack?: () => void;
}

export default function TicketCard({ booking, onBack }: Props) {
    return (
        <section className="feature-card ticket-card">
            {onBack && <button className="secondary-button" onClick={onBack}>← Back</button>}
            <div className="ticket-topline">
                <div>
                    <p className="eyebrow">RAILMATE • E-TICKET</p>
                    <h2>Booking confirmed</h2>
                </div>
                <span className="feature-status">{booking.ticketStatus}</span>
            </div>
            <div className="pnr-block">
                <span>PNR NUMBER</span>
                <strong>{booking.pnr}</strong>
            </div>
            <div className="ticket-route">
                <div>
                    <span>{booking.sourceStationCode}</span>
                    <strong>{booking.sourceStationName}</strong>
                </div>
                <span className="ticket-arrow">→</span>
                <div>
                    <span>{booking.destinationStationCode}</span>
                    <strong>{booking.destinationStationName}</strong>
                </div>
            </div>
            <div className="feature-details-grid">
                <div><small>Train</small><strong>{booking.trainNumber} - {booking.trainName}</strong></div>
                <div><small>Journey date</small><strong>{booking.journeyDate}</strong></div>
                <div><small>Booking ID</small><strong>{booking.bookingId}</strong></div>
                <div><small>Total fare</small><strong>₹{Number(booking.totalFare).toLocaleString("en-IN")}</strong></div>
                <div><small>Booking status</small><strong>{booking.bookingStatus}</strong></div>
                <div><small>Issued at</small><strong>{booking.issuedAt ? new Date(booking.issuedAt).toLocaleString() : "—"}</strong></div>
            </div>
            <h3>Passenger details</h3>
            <div className="feature-table">
                {booking.passengers.map((passenger, index) => (
                    <div className="feature-table-row" key={`${booking.bookingId}-${index}`}>
                        <span>{passenger.name} · {passenger.age} · {passenger.gender}</span>
                        <strong>{passenger.seatNumber}</strong>
                    </div>
                ))}
            </div>
        </section>
    );
}
