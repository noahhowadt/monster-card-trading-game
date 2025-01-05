package at.technikum.application.mctg.repositories;

import at.technikum.application.mctg.data.ConnectionPooler;
import at.technikum.application.mctg.entities.Card;
import at.technikum.application.mctg.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class CardRepository {
    private final ConnectionPooler connectionPooler;
    private final String CREATE_CARDS = "INSERT INTO cards (id, name, damage, package_id) VALUES (?, ?, ?, ?)";
    private final String ACQUIRE_CARDS_FROM_PACKAGE = "UPDATE cards SET package_id = null, user_id = ? WHERE package_id = ?";
    private final String DELETE_ALL = "DELETE FROM cards";

    public CardRepository(ConnectionPooler connectionPooler) {
        this.connectionPooler = connectionPooler;
    }

    public void save(ArrayList<Card> cards, UUID packageId) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(CREATE_CARDS)
        ) {

            for (int i = 0; i < cards.size(); i++) {
                preparedStatement.setObject(1, cards.get(i).getId());
                preparedStatement.setString(2, cards.get(i).getName());
                preparedStatement.setFloat(3, cards.get(i).getDamage());
                preparedStatement.setObject(4, packageId);

                preparedStatement.addBatch(); // Add the current set of parameters to the batch
            }

            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void acquireFromPackage(User user, UUID packageId) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(ACQUIRE_CARDS_FROM_PACKAGE)
        ) {

            preparedStatement.setObject(1, user.getId());
            preparedStatement.setObject(2, packageId);

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteAll() {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL)
        ) {
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
