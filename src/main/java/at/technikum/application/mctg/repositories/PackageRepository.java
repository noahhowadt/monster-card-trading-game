package at.technikum.application.mctg.repositories;

import at.technikum.application.mctg.data.ConnectionPooler;
import at.technikum.application.mctg.exceptions.NotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PackageRepository {
    private final String CREATE_PACKAGE = "INSERT INTO packages (id) VALUES (?)";
    private final String GET_RANDOM_PACKAGE = "SELECT * FROM packages ORDER BY RANDOM() LIMIT 1";
    private final String DELETE_PACKAGE = "DELETE FROM packages WHERE id = ?";
    private final String DELETE_ALL = "DELETE FROM packages";
    private final ConnectionPooler connectionPooler;

    public PackageRepository(ConnectionPooler connectionPooler) {
        this.connectionPooler = connectionPooler;
    }

    public UUID createPackage(UUID packageId) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(CREATE_PACKAGE)
        ) {
            preparedStatement.setObject(1, packageId);

            preparedStatement.execute();
            return packageId;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public UUID getRandomPackage() {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_RANDOM_PACKAGE)
        ) {
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            if (!resultSet.next()) throw new NotFoundException("No packages available");
            return resultSet.getObject("id", UUID.class);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deletePackage(UUID packageId) {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PACKAGE);
        ) {
            preparedStatement.setObject(1, packageId);

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteAll() {
        try (
                Connection connection = connectionPooler.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL);
        ) {
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
