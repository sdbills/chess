package client;

import chess.ChessGame;
import ui.Repl;

public class GameClient implements Client {
    private final ServerFacade server;
    private final int gameID;
    private final ChessGame.TeamColor color;
    private final Repl repl;

    public GameClient(ServerFacade server, int gameID, ChessGame.TeamColor color, Repl repl) {
        this.server = server;
        this.gameID = gameID;
        this.color = color;
        this.repl = repl;

        System.out.println(printGame());
    }

    @Override
    public String eval(String input) {
        return "";
    }

    public String printGame() {
        return "";
    }

}
