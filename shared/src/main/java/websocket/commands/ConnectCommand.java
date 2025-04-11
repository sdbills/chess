package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand{

    ChessGame.TeamColor color;

    public ConnectCommand(CommandType commandType, String authToken, Integer gameID, ChessGame.TeamColor color) {
        super(CommandType.CONNECT, authToken, gameID);
        this.color = color;
    }

    public ConnectCommand(CommandType commandType, String authToken, Integer gameID) {
        super(CommandType.CONNECT, authToken, gameID);
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }
}
