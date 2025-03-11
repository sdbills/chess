package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SqlGameDAO extends SqlDAO implements GameDAO{

    public SqlGameDAO() throws DataAccessException {
        String[] createStatements = {"""
        CREATE TABLE IF NOT EXISTS game (
        `gameID` int NOT NULL AUTO_INCREMENT,
        `whiteUsername` varchar(256),
        `blackUsername` varchar(256),
        `gameName` varchar(256) NOT NULL,
        `game` TEXT NOT NULL,
        PRIMARY KEY (`gameID`)
        )"""
        };
        configureDatabase(createStatements);
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(
                    "INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)",
                        RETURN_GENERATED_KEYS)) {
                statement.setString(1,game.whiteUsername());
                statement.setString(2,game.blackUsername());
                statement.setString(3,game.gameName());
                var gameJSON = new Gson().toJson(game.game());
                statement.setString(4,gameJSON);
                statement.executeUpdate();

                var resultSet = statement.getGeneratedKeys();
                var ID = 0;
                if (resultSet.next()) {
                    ID = resultSet.getInt(1);
                }
                return ID;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to add game: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(String gameName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(
                    "SELECT gameID, whiteUsername, blackUsername, game FROM game WHERE gameName=?")) {
                statement.setString(1, gameName);
                try (var res = statement.executeQuery()) {
                    if (res.next()) {
                        var gameID = res.getInt("gameID");
                        var whiteUsername = res.getString("whiteUsername");
                        var blackUsername = res.getString("blackUsername");
                        var game = new Gson().fromJson(res.getString("game"), ChessGame.class);
                        return new GameData(gameID,whiteUsername,blackUsername,gameName,game);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not retrieve game: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(
                    "SELECT gameName, whiteUsername, blackUsername, game FROM game WHERE gameID=?")) {
                statement.setInt(1, gameID);
                try (var res = statement.executeQuery()) {
                    if (res.next()) {
                        var gameName = res.getString("gameName");
                        var whiteUsername = res.getString("whiteUsername");
                        var blackUsername = res.getString("blackUsername");
                        var game = new Gson().fromJson(res.getString("game"), ChessGame.class);
                        return new GameData(gameID,whiteUsername,blackUsername,gameName,game);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not retrieve game: " + e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void clear() throws DataAccessException {

    }
}
