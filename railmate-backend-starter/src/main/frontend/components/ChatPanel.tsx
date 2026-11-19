
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

interface ChatMessage {
    role: "user" | "assistant";
    content: string;
    trains?: TrainSearchResult[];
}

interface AssistantChatResponse {
    response: string;
    trains?: TrainSearchResult[];
}

function formatTime(time: string | null): string {
    return time ? time.slice(0, 5) : "--:--";
}

export default function ChatPanel() {
    const [message, setMessage] = useState("");

    const [messages, setMessages] = useState<ChatMessage[]>([
        {
            role: "assistant",
            content: "Hello! Welcome to RailMate. How can I help you today?"
        }
    ]);

    const [loading, setLoading] = useState(false);

    const sendMessage = async () => {
        const userMessage = message.trim();

        if (!userMessage || loading) return;

        setMessages((previous) => [
            ...previous,
            { role: "user", content: userMessage }
        ]);

        setMessage("");
        setLoading(true);

        try {
            const response = await fetch("/api/assistant/chat", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    message: userMessage
                })
            });

            if (!response.ok) {
                throw new Error(`Request failed: ${response.status}`);
            }

            const data: AssistantChatResponse = await response.json();

            setMessages((previous) => [
                ...previous,
                {
                    role: "assistant",
                    content: data.response,
                    trains: data.trains ?? []
                }
            ]);

        } catch (error) {
            console.error("Chat API error:", error);

            setMessages((previous) => [
                ...previous,
                {
                    role: "assistant",
                    content: "Sorry, something went wrong. Please try again."
                }
            ]);
        } finally {
            setLoading(false);
        }
    };

    const inputRef = useRef<HTMLInputElement>(null);

    useEffect(() => {
        if (!loading) {
            inputRef.current?.focus();
        }
    }, [loading]);

    const chatMessagesRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const container = chatMessagesRef.current;

        if (container) {
            container.scrollTo({
                top: container.scrollHeight,
                behavior: "smooth"
            });
        }
    }, [messages, loading]);

    return (
        <div className="chat-container">

            <div className="chat-heading">
                <h2>RailMate AI Assistant</h2>
                <p>Ask me about trains, seat availability and bookings.</p>
            </div>

            <div className="chat-messages" ref={chatMessagesRef}>

                {messages.map((msg, index) => (
                    <div
                        key={index}
                        className={`chat-message ${msg.role}`}
                    >
                        <div className="message-label">
                            {msg.role === "user" ? "You" : "RailMate AI"}
                        </div>

                        <div className="message-content">

                            {/* Keep normal Markdown rendering */}
                            {msg.role === "assistant" ? (
                                <ReactMarkdown>
                                    {msg.content}
                                </ReactMarkdown>
                            ) : (
                                msg.content
                            )}

                            {/* Display structured train cards */}
                            {msg.trains && msg.trains.length > 0 && (
                                <div className="train-cards">

                                    {msg.trains.map((train) => (
                                        <div
                                            className="train-card"
                                            key={train.trainId}
                                        >
                                            <div className="train-card-header">
                                                <div>
                                                    <h3>{train.trainName}</h3>
                                                    <span className="train-number">
                                                        Train No. {train.trainNumber}
                                                    </span>
                                                </div>

                                                <span className="train-card-icon">
                                                    🚆
                                                </span>
                                            </div>

                                            <div className="train-route">

                                                <div className="train-station">
                                                    <span className="station-label">
                                                        FROM
                                                    </span>

                                                    <strong>
                                                        {train.sourceStationName}
                                                    </strong>

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
                                                    <span className="station-label">
                                                        TO
                                                    </span>

                                                    <strong>
                                                        {train.destinationStationName}
                                                    </strong>

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
                    disabled={loading}
                    onChange={(event) => setMessage(event.target.value)}
                    onKeyDown={(event) => {
                        if (event.key === "Enter") {
                            sendMessage();
                        }
                    }}
                />

                <button
                    onClick={sendMessage}
                    disabled={loading || !message.trim()}
                >
                    Send
                </button>
            </div>

        </div>
    );
}
