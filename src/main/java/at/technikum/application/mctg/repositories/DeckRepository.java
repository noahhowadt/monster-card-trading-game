package at.technikum.application.mctg.repositories;

import at.technikum.application.mctg.data.ConnectionPooler;
import at.technikum.application.mctg.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

public class DeckRepository {
    private final ConnectionPooler connectionPooler;
    private final static String CLEAR_DECK = "DELETE FROM deck WHERE user_id = ?";
    private final static String ADD_CARDS_TO_DECK = "INSERT INTO deck (id, user_id, card_id) VALUES (?, ?, ?)";
    private final static String GET_DECK = "SELECT * FROM deck WHERE user_id = ?";
    private final static String CLEAR_ALL = "DELETE FROM deck";

    public DeckRepository(ConnectionPooler connectionPooler) {
        this.connectionPooler = connectionPooler;
    }

    public void clearForUser(UUID userId) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(CLEAR_DECK);
        ) {

            preparedStatement.setObject(1, userId);
            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void addCardsToDeck(UUID userId, ArrayList<UUID> cardIds) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(ADD_CARDS_TO_DECK);
        ) {

            for (UUID cardId : cardIds) {
                preparedStatement.setObject(1, UUID.randomUUID());
                preparedStatement.setObject(2, userId);
                preparedStatement.setObject(3, cardId);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public ArrayList<UUID> getByUser(User user) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_DECK)
        ) {
            preparedStatement.setObject(1, user.getId());
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();

            ArrayList<UUID> deck = new ArrayList<>();
            while (resultSet.next()) {
                deck.add(resultSet.getObject("card_id", UUID.class));
            }
            return deck;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteAll() {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(CLEAR_ALL);
        ) {
            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
