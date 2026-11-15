
import { useState } from "react";
import ReactMarkdown from "react-markdown";

interface ChatMessage {
    role: "user" | "assistant";
    content: string;
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

            const data = await response.json();

            setMessages((previous) => [
                ...previous,
                {
                    role: "assistant",
                    content: data.response
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

    return (
        <div className="chat-container">

            <div className="chat-heading">
                <h2>RailMate AI Assistant</h2>
                <p>Ask me about trains, seat availability and bookings.</p>
            </div>

            <div className="chat-messages">

                {messages.map((msg, index) => (
                    <div
                        key={index}
                        className={`chat-message ${msg.role}`}
                    >
                        <div className="message-label">
                            {msg.role === "user" ? "You" : "RailMate AI"}
                        </div>

                        <div className="message-content">
                            {msg.role === "assistant" ? (
                                <ReactMarkdown>
                                    {msg.content}
                                </ReactMarkdown>
                            ) : (
                                msg.content
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
