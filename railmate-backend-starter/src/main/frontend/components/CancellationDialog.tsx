import { useState } from "react";
import { confirmCancellation, requestCancellation } from "../services/bookingService";

interface Props {
    bookingId: number;
    pnr: string;
    onCancelled: () => void;
}

export default function CancellationDialog({ bookingId, pnr, onCancelled }: Props) {
    const [token, setToken] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");


    async function prepare() {
        setLoading(true);
        setError("");

        try {
            const result = await requestCancellation(bookingId);

            setToken(result.confirmationToken);
            setMessage(result.message);

        } catch (err) {
            setError(
                err instanceof Error
                    ? err.message
                    : "Could not prepare cancellation."
            );
        } finally {
            setLoading(false);
        }
    }


    async function confirm() {
        if (!token) return;
        setLoading(true);
        setError("");
        try {
            const result = await confirmCancellation(token);
            setMessage(result.message);
            setToken("");
            onCancelled();
        } catch (err) {
            setError(err instanceof Error ? err.message : "Cancellation failed.");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="cancellation-panel">
            <p>PNR: <strong>{pnr}</strong></p>
            {error && <p className="feature-error">{error}</p>}
            {message && <p className="feature-note">{message}</p>}
            {!token ? (
                <button className="danger-button" disabled={loading} onClick={prepare}>
                    {loading ? "Preparing..." : "Request cancellation"}
                </button>
            ) : (
                <>
                    <p className="feature-warning">Please confirm: this action will cancel the booking and restore its seats.</p>
                    <div className="feature-actions">
                        <button className="danger-button" disabled={loading} onClick={confirm}>
                            {loading ? "Cancelling..." : "Confirm cancellation"}
                        </button>
                        <button className="secondary-button" disabled={loading} onClick={() => { setToken(""); setMessage(""); }}>
                            Keep booking
                        </button>
                    </div>
                </>
            )}
        </div>
    );
}
