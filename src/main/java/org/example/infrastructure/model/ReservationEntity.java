package org.example.infrastructure.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private CarEntity car;

    public ReservationEntity(final CarEntity car, final LocalDate startDate, final LocalDate endDate) {
        this.car = car;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
