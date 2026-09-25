package repository;

import model.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository {

    void save(Reservation reservation);

    Optional<Reservation> findByCode(String code);

    List<Reservation> findByUserId(UUID userId);

    List<Reservation> findAll();

    boolean existsOverlappingReservation(
            UUID roomId,
            LocalDate checkIn,
            LocalDate checkOut
    );

    void update(Reservation reservation);
}