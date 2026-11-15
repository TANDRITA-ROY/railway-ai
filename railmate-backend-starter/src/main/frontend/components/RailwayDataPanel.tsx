
import { useEffect, useState } from "react";
import type { Train } from "../types/Train";
import type { BookingDetails } from "../types/BookingDetails";
import type { SeatInventoryDetails } from "../types/SeatInventoryDetails";

export default function RailwayDataPanel() {
    const [trains, setTrains] = useState<Train[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [bookings, setBookings] = useState<BookingDetails[]>([]);
    const [bookingLoading, setBookingLoading] = useState(true);
    const [bookingError, setBookingError] = useState("");

    const [inventory, setInventory] = useState<SeatInventoryDetails[]>([]);
    const [inventoryLoading, setInventoryLoading] = useState(true);
    const [inventoryError, setInventoryError] = useState("");

    // Fetch trains
    useEffect(() => {
        const fetchTrains = async () => {
            try {
                const response = await fetch("/api/trains");

                if (!response.ok) {
                    throw new Error(`Failed to fetch trains. Status: ${response.status}`);
                }

                const data: Train[] = await response.json();
                setTrains(data);
            } catch (err) {
                setError(err instanceof Error ? err.message : "Something went wrong.");
            } finally {
                setLoading(false);
            }
        };

        fetchTrains();
    }, []);

    // Fetch bookings
    useEffect(() => {
        const fetchBookings = async () => {
            try {
                const response = await fetch("/api/bookings");

                if (!response.ok) {
                    throw new Error(
                        `Failed to fetch bookings. Status: ${response.status}`
                    );
                }

                const data: BookingDetails[] = await response.json();
                setBookings(data);
            } catch (err) {
                setBookingError(
                    err instanceof Error ? err.message : "Something went wrong."
                );
            } finally {
                setBookingLoading(false);
            }
        };

        fetchBookings();
    }, []);

    // Fetch seat inventory
    useEffect(() => {
        const fetchInventory = async () => {
            try {
                const response = await fetch("/api/seat-inventory");

                if (!response.ok) {
                    throw new Error(
                        `Failed to fetch inventory. Status: ${response.status}`
                    );
                }

                const data: SeatInventoryDetails[] = await response.json();
                setInventory(data);
            } catch (err) {
                setInventoryError(
                    err instanceof Error ? err.message : "Something went wrong."
                );
            } finally {
                setInventoryLoading(false);
            }
        };

        fetchInventory();
    }, []);

    return (
        <div className="railway-data">
            <div className="panel-heading">
                <h2>Railway Overview</h2>
                <p>Monitor your railway booking system</p>
            </div>

            {/* Train Information */}
            <div className="data-card">
                <h3>🚆 Train Information</h3>
                <p>Trains registered in the system</p>

                {loading && <p>Loading trains...</p>}
                {error && <p className="error-message">{error}</p>}

                {!loading && !error && (
                    <>
                        <p className="train-count">Total trains: {trains.length}</p>

                        {trains.length === 0 ? (
                            <p>No trains found.</p>
                        ) : (
                            <div className="train-list">
                                {trains.map((train) => (
                                    <div className="train-item" key={train.id}>
                                        <div>
                                            <strong>{train.name}</strong>
                                            <p>Train No: {train.trainNumber}</p>
                                        </div>
                                        <span
                                            className={
                                                train.active ? "status-active" : "status-inactive"
                                            }
                                        >
                      {train.active ? "Active" : "Inactive"}
                    </span>
                                    </div>
                                ))}
                            </div>
                        )}
                    </>
                )}
            </div>

            {/* Booking Information */}
            <div className="data-card">
                <h3>🎫 Booking Information</h3>
                <p>Bookings, tickets and passenger details</p>

                {bookingLoading && <p>Loading bookings...</p>}

                {bookingError && (
                    <p className="error-message">{bookingError}</p>
                )}

                {!bookingLoading && !bookingError && (
                    <>
                        <p className="train-count">
                            Total bookings: {bookings.length}
                        </p>

                        {bookings.length === 0 ? (
                            <p>No bookings found.</p>
                        ) : (
                            <div className="booking-list">
                                {bookings.map((booking) => (
                                    <div className="booking-item" key={booking.bookingId}>
                                        <div className="booking-header">
                                            <div>
                                                <strong>PNR: {booking.pnr}</strong>
                                                <p>Booking ID: {booking.bookingId}</p>
                                            </div>

                                            <span
                                                className={
                                                    booking.bookingStatus.toUpperCase() === "CONFIRMED"
                                                        ? "status-active"
                                                        : "status-inactive"
                                                }
                                            >
                        {booking.bookingStatus}
                      </span>
                                        </div>

                                        <div className="booking-train">
                                            <strong>
                                                {booking.trainNumber} - {booking.trainName}
                                            </strong>

                                            <p>
                                                {booking.sourceStationCode} -{" "}
                                                {booking.sourceStationName}
                                            </p>

                                            <p>
                                                {booking.destinationStationCode} -{" "}
                                                {booking.destinationStationName}
                                            </p>
                                        </div>

                                        <div className="booking-meta">
                                            <span>Journey: {booking.journeyDate}</span>
                                            <span>
                        Fare: ₹{booking.totalFare.toLocaleString("en-IN")}
                      </span>
                                        </div>

                                        <div className="passenger-section">
                                            <strong>
                                                Passengers ({booking.passengers.length})
                                            </strong>

                                            {booking.passengers.map((passenger, index) => (
                                                <div
                                                    className="passenger-item"
                                                    key={`${booking.bookingId}-${index}`}
                                                >
                          <span>
                            {passenger.name} ({passenger.age})
                          </span>
                                                    <span>Seat: {passenger.seatNumber}</span>
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </>
                )}
            </div>

            {/* Seat Inventory */}
            <div className="data-card">
                <h3>💺 Seat Inventory</h3>
                <p>Monitor coach-wise seat availability.</p>

                {inventoryLoading && <p>Loading inventory...</p>}

                {inventoryError && (
                    <p className="error-message">{inventoryError}</p>
                )}

                {!inventoryLoading && !inventoryError && (
                    <>
                        <p className="train-count">
                            Total inventory records: {inventory.length}
                        </p>

                        {inventory.length === 0 ? (
                            <p>No inventory records found.</p>
                        ) : (
                            <div className="inventory-list">
                                {inventory.map((item) => (
                                    <div className="inventory-item" key={item.id}>
                                        <div className="inventory-header">
                                            <strong>
                                                {item.train.trainNumber} - {item.train.name}
                                            </strong>
                                            <span className="coach-badge">
                  {item.coach.coachNumber} ({item.coach.coachType})
                </span>
                                        </div>

                                        <p>
                                            {item.fromStation.code} - {item.fromStation.name}
                                        </p>

                                        <p>
                                            {item.toStation.code} - {item.toStation.name}
                                        </p>

                                        <div className="inventory-meta">
                                            <span>Journey: {item.journeyDate}</span>
                                            <span>
                  Seats: {item.availableSeats}/{item.coach.totalSeats}
                </span>
                                        </div>

                                        <div className="inventory-footer">
                                            <strong>
                                                ₹{item.fare.toLocaleString("en-IN")}
                                            </strong>

                                            <span
                                                className={
                                                    item.availableSeats > 0
                                                        ? "status-active"
                                                        : "status-inactive"
                                                }
                                            >
                  {item.availableSeats > 0
                      ? "Available"
                      : "Sold Out"}
                </span>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </>
                )}
            </div>
        </div>
    );
}
