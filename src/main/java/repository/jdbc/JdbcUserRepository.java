package repository.jdbc;

import db.DatabaseConnection;
import model.User;
import model.enums.UserRole;
import repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class JdbcUserRepository implements UserRepository {

    private final DatabaseConnection databaseConnection =
            DatabaseConnection.getInstance();

    @Override
    public void save(User user) {

        String sql = """
                INSERT INTO users
                (id, full_name, email, phone, password_hash, ville, title)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        databaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setObject(1, user.getId());
            statement.setString(2, user.getFullName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPhone());
            statement.setString(5, user.getPasswordHash());
            statement.setString(6, user.getVille());
            statement.setString(7, user.getTitle().name());

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erreur lors de l'enregistrement de l'utilisateur.",
                    e
            );
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {

        String sql = """
                SELECT *
                FROM users
                WHERE email = ?
                """;

        try (
                Connection connection =
                        databaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, email);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                return Optional.of(
                        mapUser(resultSet)
                );
            }

            return Optional.empty();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erreur lors de la recherche de l'utilisateur.",
                    e
            );
        }
    }

    @Override
    public Optional<User> findById(UUID id) {

        String sql = """
                SELECT *
                FROM users
                WHERE id = ?
                """;

        try (
                Connection connection =
                        databaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setObject(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                return Optional.of(
                        mapUser(resultSet)
                );
            }

            return Optional.empty();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erreur lors de la recherche de l'utilisateur.",
                    e
            );
        }
    }

    private User mapUser(ResultSet resultSet)
            throws SQLException {

        return new User(
                resultSet.getObject("id", UUID.class),
                resultSet.getString("full_name"),
                resultSet.getString("email"),
                resultSet.getString("phone"),
                resultSet.getString("password_hash"),
                resultSet.getString("ville"),
                UserRole.valueOf(
                        resultSet.getString("title")
                )
        );
    }
}