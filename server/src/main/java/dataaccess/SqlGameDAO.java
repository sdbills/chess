package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class SqlGameDAO extends SqlDAO implements GameDAO{

    public SqlGameDAO() throws DataAccessException {
        String[] createStatements = {
            """
        CREATE TABLE IF NOT EXISTS game (
        `gameID` int NOT NULL AUTO_INCREMENT,
        `whiteUsername` varchar(256),
        `blackUsername` varchar(256),
        `gameName` varchar(256),
        `game` varchar(256) NOT NULL,
        PRIMARY KEY (`id`)
        )
        """
        };
        configureDatabase(createStatements);
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
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
