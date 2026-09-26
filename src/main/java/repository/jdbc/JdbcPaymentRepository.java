package repository.jdbc;

import db.DatabaseConnection;
import model.Payment;
import model.enums.PaymentStatus;
import repository.PaymentRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JdbcPaymentRepository implements PaymentRepository {

    @Override
    public void save(Payment payment) {

        String sql = """
                INSERT INTO payments
                (id, user_id, reservation_id, amount, status, payment_date)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, payment.getId());
            statement.setObject(2, payment.getUserId());
            statement.setObject(3, payment.getReservationId());
            statement.setBigDecimal(4, payment.getAmount());
            statement.setString(5, payment.getStatus().name());
            statement.setTimestamp(
                    6,
                    Timestamp.valueOf(payment.getPaymentDate())
            );

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de l'enregistrement du paiement.",
                    e
            );
        }
    }

    @Override
    public Optional<Payment> findById(UUID id) {

        String sql = """
                SELECT id, user_id, reservation_id,
                       amount, status, payment_date
                FROM payments
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, id);

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapPayment(resultSet));
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche du paiement.",
                    e
            );
        }
    }

    @Override
    public Optional<Payment> findByReservationId(
            UUID reservationId
    ) {

        String sql = """
                SELECT id, user_id, reservation_id,
                       amount, status, payment_date
                FROM payments
                WHERE reservation_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, reservationId);

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapPayment(resultSet));
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche du paiement.",
                    e
            );
        }
    }

    @Override
    public List<Payment> findByUserId(UUID userId) {

        String sql = """
                SELECT id, user_id, reservation_id,
                       amount, status, payment_date
                FROM payments
                WHERE user_id = ?
                ORDER BY payment_date DESC
                """;

        List<Payment> payments =
                new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, userId);

            ResultSet resultSet =
                    statement.executeQuery();

            while (resultSet.next()) {
                payments.add(
                        mapPayment(resultSet)
                );
            }

            return payments;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la récupération des paiements.",
                    e
            );
        }
    }

    @Override
    public List<Payment> findAll() {

        String sql = """
                SELECT id, user_id, reservation_id,
                       amount, status, payment_date
                FROM payments
                ORDER BY payment_date DESC
                """;

        List<Payment> payments =
                new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                payments.add(
                        mapPayment(resultSet)
                );
            }

            return payments;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la récupération des paiements.",
                    e
            );
        }
    }

    @Override
    public void update(Payment payment) {

        String sql = """
                UPDATE payments
                SET amount = ?,
                    status = ?,
                    payment_date = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBigDecimal(
                    1,
                    payment.getAmount()
            );

            statement.setString(
                    2,
                    payment.getStatus().name()
            );

            statement.setTimestamp(
                    3,
                    Timestamp.valueOf(
                            payment.getPaymentDate()
                    )
            );

            statement.setObject(
                    4,
                    payment.getId()
            );

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la modification du paiement.",
                    e
            );
        }
    }

    private Payment mapPayment(
            ResultSet resultSet
    ) throws SQLException {

        UUID id =
                resultSet.getObject(
                        "id",
                        UUID.class
                );

        UUID userId =
                resultSet.getObject(
                        "user_id",
                        UUID.class
                );

        UUID reservationId =
                resultSet.getObject(
                        "reservation_id",
                        UUID.class
                );

        BigDecimal amount =
                resultSet.getBigDecimal(
                        "amount"
                );

        PaymentStatus status =
                PaymentStatus.valueOf(
                        resultSet.getString("status")
                );

        LocalDateTime paymentDate =
                resultSet.getTimestamp(
                        "payment_date"
                ).toLocalDateTime();

        return new Payment(
                id,
                userId,
                reservationId,
                amount,
                status,
                paymentDate
        );
    }
}