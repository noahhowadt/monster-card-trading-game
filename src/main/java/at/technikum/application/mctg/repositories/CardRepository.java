package at.technikum.application.mctg.repositories;

import at.technikum.application.mctg.data.ConnectionPooler;
import at.technikum.application.mctg.entities.Card;
import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.exceptions.BadRequestException;

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
    private final String GET_BY_USER = "SELECT * FROM cards WHERE user_id = ?";
    private final String GET_BY_ID = "SELECT * FROM cards WHERE id = ?";
    private final String CHANGE_OWNER = "UPDATE cards SET user_id = ? WHERE id = ?";
    private final String GET_RANDOM_FROM_DECK = "SELECT * FROM cards INNER JOIN deck WHERE cards.user_id = ? ORDER BY RANDOM() LIMIT 1";

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

    public ArrayList<Card> getByUser(User user) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_USER);
        ) {
            preparedStatement.setObject(1, user.getId());
            preparedStatement.execute();

            ArrayList<Card> cards = new ArrayList<>();
            while (preparedStatement.getResultSet().next()) {
                Card newCard = new Card();
                newCard.setId(preparedStatement.getResultSet().getObject("id", UUID.class));
                newCard.setName(preparedStatement.getResultSet().getString("name"));
                newCard.setDamage(preparedStatement.getResultSet().getFloat("damage"));
                newCard.setUserId(preparedStatement.getResultSet().getObject("user_id", UUID.class));
                newCard.setPackageId(preparedStatement.getResultSet().getObject("package_id", UUID.class));
                cards.add(newCard);
            }

            return cards;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Card getById(UUID id) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID);
        ) {
            preparedStatement.setObject(1, id);
            preparedStatement.execute();

            boolean hasResult = preparedStatement.getResultSet().next();
            if (!hasResult) throw new BadRequestException("Card not found");

            Card card = new Card();
            card.setId(preparedStatement.getResultSet().getObject("id", UUID.class));
            card.setName(preparedStatement.getResultSet().getString("name"));
            card.setDamage(preparedStatement.getResultSet().getFloat("damage"));
            card.setUserId(preparedStatement.getResultSet().getObject("user_id", UUID.class));
            card.setPackageId(preparedStatement.getResultSet().getObject("package_id", UUID.class));

            return card;
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

    public void switchOwner(Card card1, Card card2) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(CHANGE_OWNER)
        ) {
            connection.setAutoCommit(false);

            // Switch owner of card 1
            preparedStatement.setObject(1, card2.getUserId());
            preparedStatement.setObject(2, card1.getId());
            preparedStatement.execute();

            // Switch owner of card 2
            preparedStatement.setObject(1, card1.getUserId());
            preparedStatement.setObject(2, card2.getId());
            preparedStatement.execute();

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Card getRandomFromDeck(User user) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_RANDOM_FROM_DECK)
        ) {
            preparedStatement.setObject(1, user.getId());
            preparedStatement.execute();

            boolean hasResult = preparedStatement.getResultSet().next();
            if (!hasResult) throw new BadRequestException("No cards in deck");

            Card card = new Card();
            card.setId(preparedStatement.getResultSet().getObject("id", UUID.class));
            card.setName(preparedStatement.getResultSet().getString("name"));
            card.setDamage(preparedStatement.getResultSet().getFloat("damage"));
            card.setUserId(preparedStatement.getResultSet().getObject("user_id", UUID.class));
            card.setPackageId(preparedStatement.getResultSet().getObject("package_id", UUID.class));

            return card;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
