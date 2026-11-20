import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import PassengerForm from "./PassengerForm";
import TicketCard from "./TicketCard";
import type { Train } from "../types/Train";
import type { PassengerRequest } from "../types/BookingRequest";
import type { BookingDetails } from "../types/BookingDetails";
import { confirmBooking, getBookingDetails, prepareBooking } from "../services/bookingService";

const now = new Date();
const today = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}-${String(now.getDate()).padStart(2, "0")}`;

export default function BookNow() {
    const [trains, setTrains] = useState<Train[]>([]);
    const [trainNumber, setTrainNumber] = useState("");
    const [source, setSource] = useState("");
    const [destination, setDestination] = useState("");
    const [journeyDate, setJourneyDate] = useState(today);
    const [coachType, setCoachType] = useState("3A");
    const [passengers, setPassengers] = useState<PassengerRequest[]>([
        { name: "", age: 0, gender: "" },
    ]);
    const [prepared, setPrepared] = useState<Awaited<ReturnType<typeof prepareBooking>> | null>(null);
    const [ticket, setTicket] = useState<BookingDetails | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        fetch("/api/trains")
            .then(async (response) => {
                if (!response.ok) throw new Error("Unable to load trains.");
                return response.json() as Promise<Train[]>;
            })
            .then((data) => {
                const active = data.filter((train) => train.active !== false);
                setTrains(active);
                if (active.length) setTrainNumber(active[0].trainNumber);
            })
            .catch((err: unknown) => setError(err instanceof Error ? err.message : "Unable to load trains."));
    }, []);

    async function handlePrepare(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setError("");
        setLoading(true);
        setTicket(null);
        try {
            const result = await prepareBooking({
                trainNumber,
                source: source.trim().toUpperCase(),
                destination: destination.trim().toUpperCase(),
                journeyDate,
                coachType,
                passengers,
            });
            setPrepared(result);
        } catch (err) {
            setError(err instanceof Error ? err.message : "Could not prepare booking.");
        } finally {
            setLoading(false);
        }
    }

    async function handleConfirm() {
        if (!prepared) return;
        setLoading(true);
        setError("");
        try {
            const result = await confirmBooking(prepared.confirmationToken);
            const details = await getBookingDetails(result.bookingId);
            setTicket(details);
            setPrepared(null);
        } catch (err) {
            setError(err instanceof Error ? err.message : "Booking confirmation failed.");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="feature-page">
            <div className="feature-page-heading">
                <div><p className="eyebrow">RESERVATIONS</p><h1>Book a train</h1>
                    <p>Enter journey and passenger details. Review the fare before confirming.</p></div>
            </div>

            {error && <div className="feature-error" role="alert">{error}</div>}

            {ticket ? (
                <TicketCard booking={ticket} onBack={() => setTicket(null)} />
            ) : prepared ? (
                <section className="feature-card">
                    <p className="eyebrow">FINAL REVIEW</p>
                    <h2>Review your booking</h2>
                    <div className="feature-details-grid">
                        <div><small>Train</small><strong>{prepared.trainNumber} - {prepared.trainName}</strong></div>
                        <div><small>Route</small><strong>{prepared.source} → {prepared.destination}</strong></div>
                        <div><small>Journey date</small><strong>{prepared.journeyDate}</strong></div>
                        <div><small>Class</small><strong>{prepared.coachType}</strong></div>
                        <div><small>Passengers</small><strong>{prepared.passengerCount}</strong></div>
                        <div><small>Estimated total fare</small><strong>₹{Number(prepared.estimatedTotalFare).toLocaleString("en-IN")}</strong></div>
                    </div>
                    <h3>Passengers</h3>
                    <ul className="review-passenger-list">
                        {prepared.passengers.map((p, i) => <li key={i}>{p.name} · {p.age} · {p.gender}</li>)}
                    </ul>
                    <p className="feature-note">The confirmation token expires after 5 minutes. Confirm only if these details are correct.</p>
                    <div className="feature-actions">
                        <button className="primary-button" disabled={loading} onClick={handleConfirm}>
                            {loading ? "Confirming..." : "Confirm and book"}
                        </button>
                        <button className="secondary-button" disabled={loading} onClick={() => setPrepared(null)}>Go back and edit</button>
                    </div>
                </section>
            ) : (
                <form className="feature-card booking-form" onSubmit={handlePrepare}>
                    <h2>Journey details</h2>
                    <div className="feature-form-grid">
                        <label>Train
                            <select required value={trainNumber} onChange={(e) => setTrainNumber(e.target.value)}>
                                {trains.length === 0 && <option value="">No active trains found</option>}
                                {trains.map((train) => <option key={train.id} value={train.trainNumber}>{train.trainNumber} - {train.name}</option>)}
                            </select>
                        </label>
                        <label>Source station code
                            <input required maxLength={10} value={source} onChange={(e) => setSource(e.target.value)} placeholder="e.g. HWH" />
                        </label>
                        <label>Destination station code
                            <input required maxLength={10} value={destination} onChange={(e) => setDestination(e.target.value)} placeholder="e.g. PNBE" />
                        </label>
                        <label>Journey date
                            <input required type="date" min={today} value={journeyDate} onChange={(e) => setJourneyDate(e.target.value)} />
                        </label>
                        <label>Coach class
                            <select value={coachType} onChange={(e) => setCoachType(e.target.value)}>
                                <option value="1A">1A - First AC</option>
                                <option value="2A">2A - Second AC</option>
                                <option value="3A">3A - Third AC</option>
                                <option value="SL">SL - Sleeper</option>
                                <option value="CC">CC - Chair Car</option>
                                <option value="2S">2S - Second Sitting</option>
                            </select>
                        </label>
                    </div>
                    <h2 className="passenger-section-title">Passenger details</h2>
                    <PassengerForm passengers={passengers} onChange={setPassengers} />
                    <div className="feature-actions">
                        <button className="primary-button" type="submit" disabled={loading || !trainNumber || !passengers.length}>
                            {loading ? "Checking availability..." : "Review booking"}
                        </button>
                    </div>
                </form>
            )}
        </div>
    );
}
