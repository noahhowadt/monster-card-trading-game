package at.technikum.application.mctg.repositories;

import at.technikum.application.mctg.data.ConnectionPooler;
import at.technikum.application.mctg.dto.UserStats;
import at.technikum.application.mctg.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class StatsRepository {
    private final static String ADD_WIN = "INSERT INTO stats (user_id, wins, losses) VALUES (?, 1, 0) ON DUPLICATE KEY UPDATE wins = wins + 1";
    private final static String ADD_LOSS = "INSERT INTO stats (user_id, wins, losses) VALUES (?, 0, 1) ON DUPLICATE KEY UPDATE losses = losses + 1";
    // select * and elo = 100 + wins * 3 - losses * 5
    private final static String GET_BY_USER = "SELECT *, 100 + 3 * wins - 5 * losses AS elo FROM stats WHERE user_id = ?";
    private final static String GET_ALL = "SELECT *, 100 + 3 * wins - 5 * losses AS elo FROM stats ORDER BY elo DESC";
    private final static String CLEAR_ALL = "DELETE FROM stats";

    private final ConnectionPooler connectionPooler;

    public StatsRepository(ConnectionPooler connectionPooler) {
        this.connectionPooler = connectionPooler;
    }

    public void addWin(UUID userId) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(ADD_WIN)
        ) {
            preparedStatement.setObject(1, userId);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void addLoss(UUID userId) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(ADD_LOSS)
        ) {
            preparedStatement.setObject(1, userId);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public UserStats getByUser(User user) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_USER)
        ) {
            preparedStatement.setObject(1, user.getId());
            preparedStatement.execute();
            boolean hasStats = preparedStatement.getResultSet().next();
            if (!hasStats) {
                return new UserStats(user.getUsername(), 100, 0, 0);
            }
            return new UserStats(
                    user.getUsername(),
                    preparedStatement.getResultSet().getInt("elo"),
                    preparedStatement.getResultSet().getInt("wins"),
                    preparedStatement.getResultSet().getInt("losses")
            );
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public ArrayList<UserStats> getAll() {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL)
        ) {
            preparedStatement.execute();
            ArrayList<UserStats> stats = new ArrayList<>();
            while (preparedStatement.getResultSet().next()) {
                stats.add(new UserStats(
                        preparedStatement.getResultSet().getString("username"),
                        preparedStatement.getResultSet().getInt("elo"),
                        preparedStatement.getResultSet().getInt("wins"),
                        preparedStatement.getResultSet().getInt("losses")
                ));
            }
            return stats;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteAll() {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(CLEAR_ALL)
        ) {
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
