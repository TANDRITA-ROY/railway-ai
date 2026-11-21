
import { useState, useEffect, useRef } from "react";
import ReactMarkdown from "react-markdown";

interface TrainSearchResult {
    trainId: number;
    trainNumber: string;
    trainName: string;
    sourceStationCode: string;
    sourceStationName: string;
    sourceArrivalTime: string | null;
    sourceDepartureTime: string | null;
    sourceDayNumber: number;
    destinationStationCode: string;
    destinationStationName: string;
    destinationArrivalTime: string | null;
    destinationDepartureTime: string | null;
    destinationDayNumber: number;
}

interface Passenger {
    name: string;
    age: number;
    gender: string;
}

interface BookingPreparationResponse {
    confirmationToken: string;
    userId: number;
    trainNumber: string;
    trainName: string;
    source: string;
    destination: string;
    journeyDate: string;
    coachType: string;
    passengerCount: number;
    passengers: Passenger[];
    estimatedTotalFare: number;
    message: string;
}

interface BookingCreationResponse {
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
    passengers: Passenger[];
    message: string;
}

interface ChatMessage {
    id: string;
    role: "user" | "assistant";
    content: string;
    trains?: TrainSearchResult[];
    pendingBooking?: BookingPreparationResponse | null;
    confirmationResult?: BookingCreationResponse;
    bookingError?: string;
    pendingCancellation?: PendingCancellation | null;
    cancellationResult?: BookingCancellationResponse;
    cancellationError?: string;
}


interface PendingCancellation {
    confirmationToken: string;
    bookingId: number;
    pnr: string;
    message: string;
}

interface BookingCancellationResponse {
    bookingId: number;
    pnr: string;
    bookingStatus: string;
    ticketStatus: string;
    message: string;
}


interface AssistantChatResponse {
    response: string;
    trains?: TrainSearchResult[];
    pendingBooking?: BookingPreparationResponse | null;
    pendingCancellation?: PendingCancellation | null;

}

function formatTime(time: string | null): string {
    return time ? time.slice(0, 5) : "--:--";
}

function formatDate(date: string): string {
    if (!date) return "";
    return new Date(`${date}T00:00:00`).toLocaleDateString("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric"
    });
}

function formatCurrency(amount: number): string {
    return new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR"
    }).format(amount);
}

export default function ChatPanel() {
    const [message, setMessage] = useState("");
    const [messages, setMessages] = useState<ChatMessage[]>([
        {
            id: "welcome",
            role: "assistant",
            content: "Hello! Welcome to RailMate. How can I help you today?"
        }
    ]);
    const [loading, setLoading] = useState(false);
    const [confirmingToken, setConfirmingToken] = useState<string | null>(null);

    const [cancellingToken, setCancellingToken] = useState<string | null>(null);

    const confirmCancellation = async (
        messageId: string,
        confirmationToken: string
    ) => {
        if (cancellingToken || confirmingToken) return;

        setCancellingToken(confirmationToken);

        setMessages(previous =>
            previous.map(msg =>
                msg.id === messageId
                    ? { ...msg, cancellationError: undefined }
                    : msg
            )
        );

        try {
            const response = await fetch("/api/bookings/cancel", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    confirmationToken
                })
            });

            if (!response.ok) {
                let errorMessage = `Cancellation failed (${response.status}).`;

                try {
                    const errorData = await response.json();
                    errorMessage =
                        errorData.message || errorData.error || errorMessage;
                } catch {
                    // Use the default error message.
                }

                throw new Error(errorMessage);
            }

            const result: BookingCancellationResponse =
                await response.json();

            setMessages(previous =>
                previous.map(msg =>
                    msg.id === messageId
                        ? {
                            ...msg,
                            cancellationResult: result,
                            cancellationError: undefined
                        }
                        : msg
                )
            );

        } catch (error) {
            const errorMessage = error instanceof Error
                ? error.message
                : "Unable to cancel this booking.";

            setMessages(previous =>
                previous.map(msg =>
                    msg.id === messageId
                        ? { ...msg, cancellationError: errorMessage }
                        : msg
                )
            );

        } finally {
            setCancellingToken(null);
        }
    };


    const sendMessage = async () => {
        const userMessage = message.trim();

        if (!userMessage || loading || confirmingToken || cancellingToken) return;

        const userChatMessage: ChatMessage = {
            id: String(Date.now()),
            role: "user",
            content: userMessage
        };

        setMessages(previous => [...previous, userChatMessage]);
        setMessage("");
        setLoading(true);

        try {
            const response = await fetch("/api/assistant/chat", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ message: userMessage })
            });

            if (!response.ok) {
                throw new Error(`Request failed: ${response.status}`);
            }

            const data: AssistantChatResponse = await response.json();

            const assistantMessage: ChatMessage = {
                id: `${Date.now()}-assistant`,
                role: "assistant",
                content: data.response,
                trains: data.trains ?? [],
                pendingBooking: data.pendingBooking ?? null,
                pendingCancellation: data.pendingCancellation ?? null
            };

            setMessages(previous => [...previous, assistantMessage]);

        } catch (error) {
            console.error("Chat API error:", error);

            setMessages(previous => [
                ...previous,
                {
                    id: `${Date.now()}-error`,
                    role: "assistant",
                    content: "Sorry, something went wrong. Please try again."
                }
            ]);
        } finally {
            setLoading(false);
        }
    };

    const confirmBooking = async (
        messageId: string,
        confirmationToken: string
    ) => {
        if (confirmingToken || cancellingToken) return;

        setConfirmingToken(confirmationToken);

        // Clear any previous error for this booking.
        setMessages(previous =>
            previous.map(msg =>
                msg.id === messageId
                    ? { ...msg, bookingError: undefined }
                    : msg
            )
        );

        try {
            const response = await fetch("/api/bookings/confirm", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    confirmationToken: confirmationToken
                })
            });

            if (!response.ok) {
                let errorMessage = `Booking confirmation failed (${response.status}).`;

                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || errorData.error || errorMessage;
                } catch {
                    // Keep the default error message if the response is not JSON.
                }

                throw new Error(errorMessage);
            }

            const result: BookingCreationResponse = await response.json();

            // Only show success after the backend confirms the booking.
            setMessages(previous =>
                previous.map(msg =>
                    msg.id === messageId
                        ? {
                            ...msg,
                            confirmationResult: result,
                            bookingError: undefined
                        }
                        : msg
                )
            );

        } catch (error) {
            console.error("Booking confirmation error:", error);

            const errorMessage = error instanceof Error
                ? error.message
                : "Unable to confirm your booking. Please try again.";

            setMessages(previous =>
                previous.map(msg =>
                    msg.id === messageId
                        ? { ...msg, bookingError: errorMessage }
                        : msg
                )
            );
        } finally {
            setConfirmingToken(null);
        }
    };

    const inputRef = useRef<HTMLInputElement>(null);

    useEffect(() => {
        if (!loading && !confirmingToken && !cancellingToken) {
            inputRef.current?.focus();
        }
    }, [loading, confirmingToken, cancellingToken]);

    const chatMessagesRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const container = chatMessagesRef.current;

        if (container) {
            container.scrollTo({
                top: container.scrollHeight,
                behavior: "smooth"
            });
        }
    }, [messages, loading, confirmingToken, cancellingToken]);

    return (
        <div className="chat-container">

            <div className="chat-heading">
                <h2>RailMate AI Assistant</h2>
                <p>Ask me about trains, seat availability and bookings.</p>
            </div>

            <div className="chat-messages" ref={chatMessagesRef}>

                {messages.map(msg => (
                    <div
                        key={msg.id}
                        className={`chat-message ${msg.role}`}
                    >
                        <div className="message-label">
                            {msg.role === "user" ? "You" : "RailMate AI"}
                        </div>

                        <div className="message-content">

                            {msg.role === "assistant" ? (
                                <ReactMarkdown>{msg.content}</ReactMarkdown>
                            ) : (
                                msg.content
                            )}

                            {/* Existing train search cards */}
                            {msg.trains && msg.trains.length > 0 && (
                                <div className="train-cards">
                                    {msg.trains.map(train => (
                                        <div className="train-card" key={train.trainId}>
                                            <div className="train-card-header">
                                                <div>
                                                    <h3>{train.trainName}</h3>
                                                    <span className="train-number">
                                                        Train No. {train.trainNumber}
                                                    </span>
                                                </div>
                                                <span className="train-card-icon">🚆</span>
                                            </div>

                                            <div className="train-route">
                                                <div className="train-station">
                                                    <span className="station-label">FROM</span>
                                                    <strong>{train.sourceStationName}</strong>
                                                    <span className="station-code">
                                                        {train.sourceStationCode}
                                                    </span>
                                                    <span className="train-time">
                                                        {formatTime(train.sourceDepartureTime)}
                                                    </span>
                                                    <span className="train-day">
                                                        Day {train.sourceDayNumber}
                                                    </span>
                                                </div>

                                                <div className="train-route-line">
                                                    <span>→</span>
                                                </div>

                                                <div className="train-station">
                                                    <span className="station-label">TO</span>
                                                    <strong>{train.destinationStationName}</strong>
                                                    <span className="station-code">
                                                        {train.destinationStationCode}
                                                    </span>
                                                    <span className="train-time">
                                                        {formatTime(train.destinationArrivalTime)}
                                                    </span>
                                                    <span className="train-day">
                                                        Day {train.destinationDayNumber}
                                                    </span>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}

                            {/* Booking review card */}
                            {msg.pendingBooking && (
                                <div className="booking-card">

                                    <div className="booking-card-header">
                                        <div>
                                            <h3>Review Your Booking</h3>
                                            <span className="booking-status-badge">
                                                Awaiting confirmation
                                            </span>
                                        </div>
                                        <span className="booking-icon">🎫</span>
                                    </div>

                                    <div className="booking-train-info">
                                        <h4>{msg.pendingBooking.trainName}</h4>
                                        <span>
                                            Train No. {msg.pendingBooking.trainNumber}
                                        </span>
                                    </div>

                                    <div className="booking-route">
                                        <div>
                                            <span className="booking-label">FROM</span>
                                            <strong>{msg.pendingBooking.source}</strong>
                                        </div>

                                        <span className="booking-route-arrow">→</span>

                                        <div>
                                            <span className="booking-label">TO</span>
                                            <strong>{msg.pendingBooking.destination}</strong>
                                        </div>
                                    </div>

                                    <div className="booking-details-grid">
                                        <div>
                                            <span className="booking-label">Journey date</span>
                                            <strong>
                                                {formatDate(msg.pendingBooking.journeyDate)}
                                            </strong>
                                        </div>

                                        <div>
                                            <span className="booking-label">Coach type</span>
                                            <strong>{msg.pendingBooking.coachType}</strong>
                                        </div>

                                        <div>
                                            <span className="booking-label">Passengers</span>
                                            <strong>{msg.pendingBooking.passengerCount}</strong>
                                        </div>
                                    </div>

                                    <div className="booking-passengers">
                                        <h4>Passenger details</h4>

                                        {msg.pendingBooking.passengers.map((passenger, index) => (
                                            <div
                                                className="booking-passenger"
                                                key={`${passenger.name}-${index}`}
                                            >
                                                <span>{index + 1}. {passenger.name}</span>
                                                <span>
                                                    {passenger.age} years · {passenger.gender}
                                                </span>
                                            </div>
                                        ))}
                                    </div>

                                    <div className="booking-fare">
                                        <span>Estimated total fare</span>
                                        <strong>
                                            {formatCurrency(
                                                msg.pendingBooking.estimatedTotalFare
                                            )}
                                        </strong>
                                    </div>



                                    {/* Successful confirmation result */}
                                    {msg.confirmationResult ? (
                                        <div className="booking-success">
                                            <div className="booking-success-heading">
                                                <span>✓</span>
                                                <strong>Booking Confirmed!</strong>
                                            </div>

                                            <p>
                                                {msg.confirmationResult.message}
                                            </p>

                                            <div className="booking-confirmed-details">
                                                <div>
                                                    <span>PNR</span>
                                                    <strong>{msg.confirmationResult.pnr}</strong>
                                                </div>

                                                <div>
                                                    <span>Booking status</span>
                                                    <strong>
                                                        {msg.confirmationResult.bookingStatus}
                                                    </strong>
                                                </div>

                                                <div>
                                                    <span>Ticket status</span>
                                                    <strong>
                                                        {msg.confirmationResult.ticketStatus}
                                                    </strong>
                                                </div>

                                                <div>
                                                    <span>Coach number</span>
                                                    <strong>
                                                        {msg.confirmationResult.coachNumber || "Pending"}
                                                    </strong>
                                                </div>

                                                <div>
                                                    <span>Final fare</span>
                                                    <strong>
                                                        {formatCurrency(
                                                            msg.confirmationResult.totalFare
                                                        )}
                                                    </strong>
                                                </div>
                                            </div>
                                        </div>
                                    ) : (
                                        <>
                                            {msg.bookingError && (
                                                <div className="booking-error">
                                                    {msg.bookingError}
                                                </div>
                                            )}

                                            <button
                                                className="confirm-booking-button"
                                                disabled={
                                                    confirmingToken !== null ||
                                                    cancellingToken !== null
                                                }
                                                onClick={() =>
                                                    confirmBooking(
                                                        msg.id,
                                                        msg.pendingBooking!.confirmationToken
                                                    )
                                                }
                                            >
                                                {confirmingToken ===
                                                msg.pendingBooking.confirmationToken
                                                    ? "Confirming..."
                                                    : "Confirm Booking"}
                                            </button>

                                            <p className="booking-note">
                                                Your booking will be created only after
                                                you confirm.
                                            </p>
                                        </>
                                    )}


                                </div>
                            )}

                            {/* Independent cancellation review card */}
                            {msg.pendingCancellation && (
                                <div className="booking-card cancellation-card">
                                    <div className="booking-card-header">
                                        <div>
                                            <h3>Review Booking Cancellation</h3>
                                            <span className="cancellation-status-badge">
                                                Awaiting confirmation
                                            </span>
                                        </div>
                                        <span className="booking-icon">⚠️</span>
                                    </div>

                                    <div className="cancellation-details">
                                        <div>
                                            <span className="booking-label">Booking ID</span>
                                            <strong>{msg.pendingCancellation.bookingId}</strong>
                                        </div>
                                        <div>
                                            <span className="booking-label">PNR</span>
                                            <strong>{msg.pendingCancellation.pnr}</strong>
                                        </div>
                                    </div>

                                    <p className="cancellation-warning">
                                        Please review the booking ID and PNR carefully.
                                        Confirming will cancel this booking.
                                    </p>

                                    {msg.cancellationResult ? (
                                        <div className="cancellation-success">
                                            <div className="booking-success-heading">
                                                <span>✓</span>
                                                <strong>Cancellation Confirmed!</strong>
                                            </div>

                                            <p>{msg.cancellationResult.message}</p>

                                            <div className="booking-confirmed-details">
                                                <div>
                                                    <span>PNR</span>
                                                    <strong>{msg.cancellationResult.pnr}</strong>
                                                </div>
                                                <div>
                                                    <span>Booking ID</span>
                                                    <strong>{msg.cancellationResult.bookingId}</strong>
                                                </div>
                                                <div>
                                                    <span>Booking status</span>
                                                    <strong>{msg.cancellationResult.bookingStatus}</strong>
                                                </div>
                                                <div>
                                                    <span>Ticket status</span>
                                                    <strong>{msg.cancellationResult.ticketStatus}</strong>
                                                </div>
                                            </div>
                                        </div>
                                    ) : (
                                        <>
                                            {msg.cancellationError && (
                                                <div className="booking-error">
                                                    {msg.cancellationError}
                                                </div>
                                            )}

                                            <button
                                                className="confirm-cancellation-button"
                                                disabled={
                                                    cancellingToken !== null ||
                                                    confirmingToken !== null
                                                }
                                                onClick={() =>
                                                    confirmCancellation(
                                                        msg.id,
                                                        msg.pendingCancellation!.confirmationToken
                                                    )
                                                }
                                            >
                                                {cancellingToken ===
                                                msg.pendingCancellation.confirmationToken
                                                    ? "Cancelling..."
                                                    : "Confirm Cancellation"}
                                            </button>

                                            <p className="booking-note">
                                                Your booking will only be cancelled after
                                                you confirm.
                                            </p>
                                        </>
                                    )}
                                </div>
                            )}

                        </div>
                    </div>
                ))}

                {loading && (
                    <div className="chat-message assistant">
                        <div className="message-label">RailMate AI</div>
                        <div className="message-content typing">
                            Thinking...
                        </div>
                    </div>
                )}
            </div>

            <div className="chat-input-area">
                <input
                    ref={inputRef}
                    type="text"
                    placeholder="Ask RailMate anything..."
                    value={message}
                    disabled={
                        loading ||
                        confirmingToken !== null ||
                        cancellingToken !== null
                    }
                    onChange={event => setMessage(event.target.value)}
                    onKeyDown={event => {
                        if (event.key === "Enter") sendMessage();
                    }}
                />

                <button
                    onClick={sendMessage}
                    disabled={
                        loading ||
                        confirmingToken !== null ||
                        cancellingToken !== null ||
                        !message.trim()
                    }
                >
                    Send
                </button>
            </div>
        </div>
    );
}
