package com.railmate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
        name = "seat_reservations",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"inventory_id", "seat_number"}
                )
        }
)
public class SeatReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_id", nullable = false)
    private SeatInventory inventory;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passenger_id", nullable = false, unique = true)
    private Passenger passenger;

    @NotBlank
    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    public SeatReservation() {
    }

    public Long getId() {
        return id;
    }

    public SeatInventory getInventory() {
        return inventory;
    }

    public void setInventory(SeatInventory inventory) {
        this.inventory = inventory;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
}