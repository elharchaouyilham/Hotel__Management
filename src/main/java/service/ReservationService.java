package service;

import model.Reservation;
import model.Room;
import model.User;
import model.enums.ReservationStatus;
import model.enums.RoomStatus;
import model.enums.RoomType;
import repository.ReservationRepository;
import repository.RoomRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            RoomRepository roomRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    public Reservation createReservation(
            User currentUser,
            UUID roomId,
            LocalDate checkIn,
            LocalDate checkOut,
            int guestsCount
    ) {

        if (currentUser == null) {
            throw new IllegalArgumentException(
                    "Vous devez être connecté."
            );
        }

        if (roomId == null) {
            throw new IllegalArgumentException(
                    "La chambre est obligatoire."
            );
        }

        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException(
                    "Les dates sont obligatoires."
            );
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException(
                    "La date de départ doit être après la date d'arrivée."
            );
        }

        if (guestsCount <= 0) {
            throw new IllegalArgumentException(
                    "Le nombre de personnes doit être supérieur à 0."
            );
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Chambre introuvable."
                        )
                );

        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new IllegalArgumentException(
                    "Cette chambre n'est pas disponible."
            );
        }

        int capacity = getRoomCapacity(
                room.getType()
        );

        if (guestsCount > capacity) {
            throw new IllegalArgumentException(
                    "Cette chambre peut accueillir maximum "
                            + capacity
                            + " personne(s)."
            );
        }

        boolean alreadyReserved =
                reservationRepository.existsOverlappingReservation(
                        roomId,
                        checkIn,
                        checkOut
                );

        if (alreadyReserved) {
            throw new IllegalArgumentException(
                    "Cette chambre est déjà réservée pour cette période."
            );
        }

        long numberOfNights =
                ChronoUnit.DAYS.between(
                        checkIn,
                        checkOut
                );

        BigDecimal totalPrice =
                room.getPrice().multiply(
                        BigDecimal.valueOf(numberOfNights)
                );

        String code = generateReservationCode();

        Reservation reservation = new Reservation(
                UUID.randomUUID(),
                code,
                currentUser.getId(),
                roomId,
                checkIn,
                checkOut,
                guestsCount,
                totalPrice,
                ReservationStatus.CONFIRMED
        );

        reservationRepository.save(
                reservation
        );

        return reservation;
    }

    public List<Reservation> getMyReservations(
            User currentUser
    ) {

        checkUser(currentUser);

        return reservationRepository.findByUserId(
                currentUser.getId()
        );
    }

    public List<Reservation> getAllReservations(
            User currentUser
    ) {

        checkUser(currentUser);

        return reservationRepository.findAll();
    }

    public Reservation getReservationByCode(
            User currentUser,
            String code
    ) {

        checkUser(currentUser);

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(
                    "Le code de réservation est obligatoire."
            );
        }

        return reservationRepository.findByCode(
                code.trim()
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "Réservation introuvable."
                )
        );
    }

    public void cancelReservation(
            User currentUser,
            String code
    ) {

        checkUser(currentUser);

        Reservation reservation =
                reservationRepository.findByCode(
                        code
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Réservation introuvable."
                        )
                );

        if (!reservation.getUserId()
                .equals(currentUser.getId())) {

            throw new IllegalArgumentException(
                    "Vous ne pouvez pas annuler cette réservation."
            );
        }

        if (reservation.getStatus()
                != ReservationStatus.CONFIRMED) {

            throw new IllegalArgumentException(
                    "Cette réservation ne peut plus être annulée."
            );
        }

        reservation.setStatus(
                ReservationStatus.CANCELLED
        );

        reservationRepository.update(
                reservation
        );
    }

    public void updateReservation(
            User currentUser,
            String code,
            LocalDate newCheckIn,
            LocalDate newCheckOut,
            int newGuestsCount
    ) {

        checkUser(currentUser);

        Reservation reservation =
                reservationRepository.findByCode(
                        code
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Réservation introuvable."
                        )
                );

        if (!reservation.getUserId()
                .equals(currentUser.getId())) {

            throw new IllegalArgumentException(
                    "Vous ne pouvez pas modifier cette réservation."
            );
        }

        if (reservation.getStatus()
                != ReservationStatus.CONFIRMED) {

            throw new IllegalArgumentException(
                    "Cette réservation ne peut plus être modifiée."
            );
        }

        if (newCheckIn == null || newCheckOut == null) {
            throw new IllegalArgumentException(
                    "Les dates sont obligatoires."
            );
        }

        if (!newCheckOut.isAfter(newCheckIn)) {
            throw new IllegalArgumentException(
                    "La date de départ doit être après la date d'arrivée."
            );
        }

        if (newGuestsCount <= 0) {
            throw new IllegalArgumentException(
                    "Le nombre de personnes doit être supérieur à 0."
            );
        }

        Room room =
                roomRepository.findById(
                        reservation.getRoomId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Chambre introuvable."
                        )
                );

        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new IllegalArgumentException(
                    "Cette chambre n'est pas disponible."
            );
        }

        int capacity =
                getRoomCapacity(
                        room.getType()
                );

        if (newGuestsCount > capacity) {
            throw new IllegalArgumentException(
                    "Cette chambre peut accueillir maximum "
                            + capacity
                            + " personne(s)."
            );
        }

        boolean alreadyReserved =
                reservationRepository.existsOverlappingReservation(
                        reservation.getRoomId(),
                        newCheckIn,
                        newCheckOut
                );

        if (alreadyReserved) {

            if (!samePeriod(
                    reservation,
                    newCheckIn,
                    newCheckOut
            )) {

                throw new IllegalArgumentException(
                        "Cette chambre est déjà réservée pour cette période."
                );
            }
        }

        long numberOfNights =
                ChronoUnit.DAYS.between(
                        newCheckIn,
                        newCheckOut
                );

        BigDecimal totalPrice =
                room.getPrice().multiply(
                        BigDecimal.valueOf(numberOfNights)
                );

        reservation.setCheckIn(
                newCheckIn
        );

        reservation.setCheckOut(
                newCheckOut
        );

        reservation.setGuestsCount(
                newGuestsCount
        );

        reservation.setTotalPrice(
                totalPrice
        );

        reservationRepository.update(
                reservation
        );
    }

    private boolean samePeriod(
            Reservation reservation,
            LocalDate checkIn,
            LocalDate checkOut
    ) {

        return reservation.getCheckIn()
                .equals(checkIn)
                &&
                reservation.getCheckOut()
                        .equals(checkOut);
    }

    private int getRoomCapacity(
            RoomType roomType
    ) {

        return switch (roomType) {

            case SINGLE -> 1;

            case DOUBLE -> 2;

            case SUITE -> 3;
        };
    }

    private String generateReservationCode() {

        int number =
                (int) (Math.random() * 9000) + 1000;

        return "RES-"
                + LocalDate.now().getYear()
                + "-"
                + number;
    }

    private void checkUser(
            User currentUser
    ) {

        if (currentUser == null) {
            throw new IllegalArgumentException(
                    "Vous devez être connecté."
            );
        }
    }
}