package at.technikum.application.mctg.repositories;

import at.technikum.application.mctg.data.ConnectionPooler;
import at.technikum.application.mctg.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class UserRepository {
    private final static String SAVE_USER
            = "INSERT INTO users (id, username, password, coins, name, bio, image) VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT(id) DO UPDATE SET coins=EXCLUDED.coins, name=EXCLUDED.name, bio=EXCLUDED.bio, image=EXCLUDED.image";
    private final static String USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private final static String USER_BY_USERNAME = "SELECT * FROM users WHERE username = ?";
    private final static String DELETE_ALL_USERS = "DELETE FROM users";

    private final ConnectionPooler connectionPooler;

    public UserRepository(ConnectionPooler connectionPooler) {
        this.connectionPooler = connectionPooler;
    }

    public User save(User user) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SAVE_USER)
        ) {
            preparedStatement.setObject(1, user.getId());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setInt(4, user.getCoins());
            preparedStatement.setString(5, user.getName());
            preparedStatement.setString(6, user.getBio());
            preparedStatement.setString(7, user.getImage());

            preparedStatement.execute();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Optional<User> findById(String id) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(USER_BY_ID);
        ) {
            preparedStatement.setString(1, id);

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet.next()) { // Process the result
                User user = new User(
                        UUID.fromString(resultSet.getString("id")),
                        resultSet.getString("username"),
                        resultSet.getString("password")
                );
                user.setCoins(resultSet.getInt("coins"));
                user.setName(resultSet.getString("name"));
                user.setBio(resultSet.getString("bio"));
                user.setImage(resultSet.getString("image"));
                return Optional.of(user); // Return the user
            } else {
                return Optional.empty(); // No user found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Optional<User> findByUsername(String username) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(USER_BY_USERNAME);
        ) {
            preparedStatement.setString(1, username);

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet.next()) { // Process the result
                User user = new User(
                        UUID.fromString(resultSet.getString("id")),
                        resultSet.getString("username"),
                        resultSet.getString("password")
                );
                user.setCoins(resultSet.getInt("coins"));
                user.setName(resultSet.getString("name"));
                user.setBio(resultSet.getString("bio"));
                user.setImage(resultSet.getString("image"));
                return Optional.of(user); // Return the user
            } else {
                return Optional.empty(); // No user found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteAll() {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL_USERS);
        ) {
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
