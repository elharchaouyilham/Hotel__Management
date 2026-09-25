package repository.jdbc;

import db.DatabaseConnection;
import model.Reservation;
import model.enums.ReservationStatus;
import repository.ReservationRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JdbcReservationRepository implements ReservationRepository {

    private final DatabaseConnection databaseConnection;

    public JdbcReservationRepository() {
        this.databaseConnection =
                DatabaseConnection.getInstance();
    }

    @Override
    public void save(Reservation reservation) {

        String sql = """
                INSERT INTO reservations
                (
                    id,
                    code,
                    user_id,
                    room_id,
                    check_in,
                    check_out,
                    guests_count,
                    total_price,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        databaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setObject(
                    1,
                    reservation.getId()
            );

            statement.setString(
                    2,
                    reservation.getCode()
            );

            statement.setObject(
                    3,
                    reservation.getUserId()
            );

            statement.setObject(
                    4,
                    reservation.getRoomId()
            );

            statement.setDate(
                    5,
                    Date.valueOf(
                            reservation.getCheckIn()
                    )
            );

            statement.setDate(
                    6,
                    Date.valueOf(
                            reservation.getCheckOut()
                    )
            );

            statement.setInt(
                    7,
                    reservation.getGuestsCount()
            );

            statement.setBigDecimal(
                    8,
                    reservation.getTotalPrice()
            );

            statement.setString(
                    9,
                    reservation.getStatus().name()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erreur lors de la création de la réservation.",
                    e
            );
        }
    }

    @Override
    public Optional<Reservation> findByCode(
            String code
    ) {

        String sql = """
                SELECT *
                FROM reservations
                WHERE code = ?
                """;

        try (
                Connection connection =
                        databaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    code
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    return Optional.of(
                            mapReservation(resultSet)
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erreur lors de la recherche de la réservation.",
                    e
            );
        }

        return Optional.empty();
    }

    @Override
    public List<Reservation> findByUserId(
            UUID userId
    ) {

        String sql = """
                SELECT *
                FROM reservations
                WHERE user_id = ?
                ORDER BY check_in DESC
                """;

        List<Reservation> reservations =
                new ArrayList<>();

        try (
                Connection connection =
                        databaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setObject(
                    1,
                    userId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    reservations.add(
                            mapReservation(resultSet)
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erreur lors de la récupération des réservations.",
                    e
            );
        }

        return reservations;
    }

    @Override
    public List<Reservation> findAll() {

        String sql = """
                SELECT *
                FROM reservations
                ORDER BY check_in DESC
                """;

        List<Reservation> reservations =
                new ArrayList<>();

        try (
                Connection connection =
                        databaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                reservations.add(
                        mapReservation(resultSet)
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erreur lors de la récupération des réservations.",
                    e
            );
        }

        return reservations;
    }

    @Override
    public boolean existsOverlappingReservation(
            UUID roomId,
            LocalDate checkIn,
            LocalDate checkOut
    ) {

        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM reservations
                    WHERE room_id = ?
                    AND status = 'CONFIRMED'
                    AND check_in < ?
                    AND check_out > ?
                )
                """;

        try (
                Connection connection =
                        databaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setObject(
                    1,
                    roomId
            );

            statement.setDate(
                    2,
                    Date.valueOf(checkOut)
            );

            statement.setDate(
                    3,
                    Date.valueOf(checkIn)
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    return resultSet.getBoolean(1);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erreur lors de la vérification de disponibilité.",
                    e
            );
        }

        return false;
    }

    @Override
    public void update(
            Reservation reservation
    ) {

        String sql = """
                UPDATE reservations
                SET
                    check_in = ?,
                    check_out = ?,
                    guests_count = ?,
                    total_price = ?,
                    status = ?
                WHERE id = ?
                """;

        try (
                Connection connection =
                        databaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setDate(
                    1,
                    Date.valueOf(
                            reservation.getCheckIn()
                    )
            );

            statement.setDate(
                    2,
                    Date.valueOf(
                            reservation.getCheckOut()
                    )
            );

            statement.setInt(
                    3,
                    reservation.getGuestsCount()
            );

            statement.setBigDecimal(
                    4,
                    reservation.getTotalPrice()
            );

            statement.setString(
                    5,
                    reservation.getStatus().name()
            );

            statement.setObject(
                    6,
                    reservation.getId()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erreur lors de la modification de la réservation.",
                    e
            );
        }
    }

    private Reservation mapReservation(
            ResultSet resultSet
    ) throws SQLException {

        return new Reservation(

                resultSet.getObject(
                        "id",
                        UUID.class
                ),

                resultSet.getString(
                        "code"
                ),

                resultSet.getObject(
                        "user_id",
                        UUID.class
                ),

                resultSet.getObject(
                        "room_id",
                        UUID.class
                ),

                resultSet.getDate(
                        "check_in"
                ).toLocalDate(),

                resultSet.getDate(
                        "check_out"
                ).toLocalDate(),

                resultSet.getInt(
                        "guests_count"
                ),

                resultSet.getBigDecimal(
                        "total_price"
                ),

                ReservationStatus.valueOf(
                        resultSet.getString(
                                "status"
                        )
                )
        );
    }
}