
import "./RailMateView.css";
import RailwayDataPanel from "../components/RailwayDataPanel";
import ChatPanel from "../components/ChatPanel";

export default function RailMateView() {
    return (
        <div className="railmate-dashboard">
            <header className="railmate-header">
                <div>
                    <div className="railmate-brand">RailMate</div>
                    <div className="railmate-subtitle">
                        Your intelligent railway booking platform
                    </div>
                </div>
            </header>

            <main className="railmate-content">
                <section className="railmate-panel">
                    <RailwayDataPanel />
                </section>

                <section className="railmate-panel">
                    <ChatPanel />
                </section>
            </main>
        </div>
    );
}
