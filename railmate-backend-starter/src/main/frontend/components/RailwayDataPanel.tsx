
export default function RailwayDataPanel() {
    return (
        <div className="railway-data">
            <div className="panel-heading">
                <h2>Railway Overview</h2>
                <p>Monitor your railway booking system</p>
            </div>

            <div className="data-card">
                <h3>🚆 Train Information</h3>
                <p>View available trains and routes.</p>
                <span>Explore Trains →</span>
            </div>

            <div className="data-card">
                <h3>🎫 Booking Information</h3>
                <p>View bookings, passengers and tickets.</p>
                <span>Manage Bookings →</span>
            </div>

            <div className="data-card">
                <h3>💺 Seat Inventory</h3>
                <p>Monitor coach-wise seat availability.</p>
                <span>View Inventory →</span>
            </div>
        </div>
    );
}
