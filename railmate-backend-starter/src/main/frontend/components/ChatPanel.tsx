
import { useState } from "react";

export default function ChatPanel() {
    const [message, setMessage] = useState("");

    return (
        <div>
            <h2>RailMate AI Assistant</h2>
            <p>Ask me about trains, seat availability and bookings.</p>

            <div>
                <p>AI: Hello! Welcome to RailMate. How can I help you today?</p>
            </div>

            <div>
                <input
                    type="text"
                    placeholder="Ask RailMate anything..."
                    value={message}
                    onChange={(event) => setMessage(event.target.value)}
                />

                <button onClick={() => console.log(message)}>
                    Send
                </button>
            </div>
        </div>
    );
}
