import type { PassengerRequest } from "../types/BookingRequest";

interface Props {
    passengers: PassengerRequest[];
    onChange: (passengers: PassengerRequest[]) => void;
}

export default function PassengerForm({ passengers, onChange }: Props) {
    function update(index: number, field: keyof PassengerRequest, value: string) {
        onChange(passengers.map((passenger, i) => {
            if (i !== index) return passenger;
            return {
                ...passenger,
                [field]: field === "age" ? Number(value) : value,
            };
        }));
    }

    return (
        <div className="passenger-form-list">
            {passengers.map((passenger, index) => (
                <div className="passenger-form-card" key={index}>
                    <div className="passenger-form-heading">
                        <strong>Passenger {index + 1}</strong>
                        {passengers.length > 1 && (
                            <button
                                type="button"
                                className="text-button danger-text"
                                onClick={() => onChange(passengers.filter((_, i) => i !== index))}
                            >
                                Remove
                            </button>
                        )}
                    </div>
                    <label>
                        Full name
                        <input
                            required
                            value={passenger.name}
                            onChange={(e) => update(index, "name", e.target.value)}
                            placeholder="Passenger name"
                        />
                    </label>
                    <div className="passenger-fields-row">
                        <label>
                            Age
                            <input
                                required
                                type="number"
                                min="1"
                                max="120"
                                value={passenger.age || ""}
                                onChange={(e) => update(index, "age", e.target.value)}
                            />
                        </label>
                        <label>
                            Gender
                            <select
                                required
                                value={passenger.gender}
                                onChange={(e) => update(index, "gender", e.target.value)}
                            >
                                <option value="">Select</option>
                                <option value="Male">Male</option>
                                <option value="Female">Female</option>
                                <option value="Other">Other</option>
                            </select>
                        </label>
                    </div>
                </div>
            ))}
            <button
                type="button"
                className="secondary-button"
                onClick={() => onChange([...passengers, { name: "", age: 0, gender: "" }])}
            >
                + Add passenger
            </button>
        </div>
    );
}
