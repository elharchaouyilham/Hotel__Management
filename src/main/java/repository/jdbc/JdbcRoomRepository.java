package repository.jdbc;

import db.DatabaseConnection;
import model.Room;
import model.enums.RoomStatus;
import model.enums.RoomType;
import repository.RoomRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JdbcRoomRepository implements RoomRepository {

    private final DatabaseConnection databaseConnection =
            DatabaseConnection.getInstance();

    @Override
    public void save(Room room) {

        String sql = """
                INSERT INTO rooms
                (id, number, type, price, status)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setObject(1, room.getId());
            statement.setInt(2, room.getNumber());
            statement.setString(3, room.getType().name());
            statement.setBigDecimal(4, room.getPrice());
            statement.setString(5, room.getStatus().name());

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erreur lors de l'ajout de la chambre.",
                    e
            );
        }
    }

    @Override
    public Optional<Room> findById(UUID id) {

        String sql = """
                SELECT *
                FROM rooms
                WHERE id = ?
                """;

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setObject(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRoom(resultSet));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erreur lors de la recherche de la chambre.",
                    e
            );
        }
    }

    @Override
    public List<Room> findAll() {

        String sql = """
                SELECT *
                FROM rooms
                ORDER BY number
                """;

        List<Room> rooms = new ArrayList<>();

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                rooms.add(mapRoom(resultSet));
            }

            return rooms;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erreur lors de la récupération des chambres.",
                    e
            );
        }
    }

    @Override
    public List<Room> findAvailableRooms() {

        String sql = """
                SELECT *
                FROM rooms
                WHERE status = 'AVAILABLE'
                ORDER BY number
                """;

        List<Room> rooms = new ArrayList<>();

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                rooms.add(mapRoom(resultSet));
            }

            return rooms;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erreur lors de la récupération des chambres disponibles.",
                    e
            );
        }
    }

    @Override
    public void update(Room room) {

        String sql = """
                UPDATE rooms
                SET number = ?,
                    type = ?,
                    price = ?,
                    status = ?
                WHERE id = ?
                """;

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, room.getNumber());
            statement.setString(2, room.getType().name());
            statement.setBigDecimal(3, room.getPrice());
            statement.setString(4, room.getStatus().name());
            statement.setObject(5, room.getId());

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erreur lors de la modification de la chambre.",
                    e
            );
        }
    }

    @Override
    public void delete(UUID id) {

        String sql = """
                DELETE FROM rooms
                WHERE id = ?
                """;

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setObject(1, id);

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erreur lors de la suppression de la chambre.",
                    e
            );
        }
    }

    private Room mapRoom(ResultSet resultSet)
            throws SQLException {

        return new Room(
                resultSet.getObject("id", UUID.class),
                resultSet.getInt("number"),
                RoomType.valueOf(
                        resultSet.getString("type")
                ),
                resultSet.getBigDecimal("price"),
                RoomStatus.valueOf(
                        resultSet.getString("status")
                )
        );
    }
}