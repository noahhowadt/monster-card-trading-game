package at.technikum.application.mctg.repositories;

import at.technikum.application.mctg.data.ConnectionPooler;
import at.technikum.application.mctg.entities.TradingDeal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

public class TradeRepository {
    private final ConnectionPooler connectionPooler;
    private final static String ADD_TRADE = "INSERT INTO trades (id, card_to_trade, type, minimum_damage) VALUES (?, ?, ?, ?)";
    private final static String GET_ALL_TRADES = "SELECT * FROM trades";
    private final static String DELETE_TRADE = "DELETE FROM trades WHERE id = ?";
    private final static String GET_TRADE_BY_ID = "SELECT * FROM trades WHERE id = ?";

    public TradeRepository(ConnectionPooler connectionPooler) {
        this.connectionPooler = connectionPooler;
    }

    public void createTrade(TradingDeal deal) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(ADD_TRADE);
        ) {
            preparedStatement.setObject(1, deal.getId());
            preparedStatement.setObject(2, deal.getCardToTrade());
            preparedStatement.setObject(3, deal.getType().toString(), java.sql.Types.OTHER);
            preparedStatement.setFloat(4, deal.getMinimumDamage());

            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public ArrayList<TradingDeal> listAll() {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_TRADES);
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<TradingDeal> deals = new ArrayList<TradingDeal>();
            while (resultSet.next()) {
                TradingDeal deal = new TradingDeal();
                deal.setId((java.util.UUID) resultSet.getObject("id"));
                deal.setCardToTrade((java.util.UUID) resultSet.getObject("card_to_trade"));
                deal.setType(at.technikum.application.mctg.entities.CardType.fromValue(resultSet.getString("type")));
                deal.setMinimumDamage(resultSet.getFloat("minimum_damage"));

                deals.add(deal);
            }

            return deals;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteById(UUID tradeId) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TRADE);
        ) {
            preparedStatement.setObject(1, tradeId);

            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public TradingDeal getById(UUID tradeId) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_TRADE_BY_ID);
        ) {
            preparedStatement.setObject(1, tradeId);
            preparedStatement.execute();

            ResultSet resultSet = preparedStatement.getResultSet();
            if (!resultSet.next()) {
                throw new RuntimeException("Trade not found");
            }

            TradingDeal deal = new TradingDeal();
            deal.setId((java.util.UUID) resultSet.getObject("id"));
            deal.setCardToTrade((java.util.UUID) resultSet.getObject("card_to_trade"));
            deal.setType(at.technikum.application.mctg.entities.CardType.fromValue(resultSet.getString("type")));
            deal.setMinimumDamage(resultSet.getFloat("minimum_damage"));

            return deal;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
