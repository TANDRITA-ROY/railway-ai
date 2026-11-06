# RailMate Backend Starter

Spring Boot 4.1.2 / Java 21 learning starter with PostgreSQL.

## Modules included
User, Station, Train, TrainStop, Coach, SeatInventory, Booking, Passenger, Ticket.
Each has an Entity, JpaRepository, Service, and REST Controller. Global exception handling
returns 400 for validation/bad JSON, 404 for missing records, and 409 for database constraint conflicts.

## Database model
- TrainStop references Train and Station (ordered stop on a route).
- Coach references Train.
- SeatInventory references Train, Coach, origin Station, destination Station and journey date.
- Booking references User, Train, origin Station and destination Station.
- Passenger references Booking.
- Ticket has a one-to-one reference to Booking.

Relationships are intentionally represented as references to existing entities. For example,
create a train first, then pass `{"train":{"id":1}}` when creating a coach.

## Run locally
1. Start PostgreSQL with `docker compose up -d`.
2. Run `mvn spring-boot:run` or launch `RailmateApplication` from IntelliJ.
3. API base URL: http://localhost:8080.

Default local DB settings are in `application.properties`. Override using DB_URL, DB_USERNAME,
and DB_PASSWORD environment variables. Do not commit real credentials.

## Endpoints
- `/api/users`
- `/api/stations`
- `/api/trains`
- `/api/train-stops`
- `/api/coaches`
- `/api/seat-inventory`
- `/api/bookings`
- `/api/passengers`
- `/api/tickets`

Each supports POST, GET collection, GET by ID, PUT by ID, DELETE by ID.

## Example payloads
User:
`{"name":"Tandrita","email":"tandrita@example.com","phone":"9876543210"}`

Station:
`{"code":"HWH","name":"Howrah Junction","city":"Howrah","state":"West Bengal"}`

Train:
`{"trainNumber":"12301","name":"Howrah Rajdhani","active":true}`

Train stop (create referenced train/station first):
`{"train":{"id":1},"station":{"id":1},"stopNumber":1,"arrivalTime":null,"departureTime":"16:50:00","dayNumber":1}`

Coach:
`{"train":{"id":1},"coachNumber":"A1","coachType":"AC_2_TIER","totalSeats":54}`

Seat inventory:
`{"train":{"id":1},"coach":{"id":1},"fromStation":{"id":1},"toStation":{"id":2},"journeyDate":"2026-12-01","availableSeats":54,"fare":1850.00}`

Booking:
`{"user":{"id":1},"train":{"id":1},"fromStation":{"id":1},"toStation":{"id":2},"journeyDate":"2026-12-01","status":"PENDING","totalFare":1850.00}`

Passenger:
`{"booking":{"id":1},"name":"Asha Roy","age":27,"gender":"FEMALE","seatNumber":null}`

Ticket:
`{"booking":{"id":1},"pnr":"RM12345678","issuedAt":"2026-11-01T12:00:00","status":"CONFIRMED"}`

## Important limitations
This is a starter scaffold, not a production-ready reservation engine. It does not yet implement
authentication, payment, seat locking, booking transactions, cancellation, RAC/waitlist, or live
railway integrations. The APIs expose entities for learning convenience; introduce DTOs before
production. `ddl-auto=update` is development-only; use Flyway migrations for production.
