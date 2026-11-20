import { useState } from "react";
import "./RailMateView.css";
import "./booking-features.css";
import RailwayDataPanel from "../components/RailwayDataPanel";
import ChatPanel from "../components/ChatPanel";
import BookNow from "../components/BookNow";
import MyBookings from "../components/MyBookings";

type Page = "dashboard" | "book" | "bookings";

export default function RailMateView() {
    const [page, setPage] = useState<Page>("dashboard");

    return (
        <div className="railmate-dashboard">
            <header className="railmate-header feature-header">
                <div>
                    <div className="railmate-brand">RailMate</div>
                    <div className="railmate-subtitle">Your intelligent railway booking platform</div>
                </div>
                <nav className="feature-nav" aria-label="Main navigation">
                    <button className={page === "dashboard" ? "active" : ""} onClick={() => setPage("dashboard")}>Dashboard</button>
                    <button className={page === "book" ? "active" : ""} onClick={() => setPage("book")}>Book Now</button>
                    <button className={page === "bookings" ? "active" : ""} onClick={() => setPage("bookings")}>My Bookings</button>
                </nav>
            </header>

            {page === "dashboard" && (
                <main className="railmate-content">
                    <section className="railmate-panel"><RailwayDataPanel /></section>
                    <section className="railmate-panel"><ChatPanel /></section>
                </main>
            )}
            {page === "book" && (
                <main className="feature-main"><BookNow /></main>
            )}
            {page === "bookings" && (
                <main className="feature-main"><MyBookings /></main>
            )}
        </div>
    );
}
