package client;

public class GameClient implements Client {
    private final ServerFacade server;
    private final boolean isWhite;

    public GameClient(ServerFacade server, boolean isWhite) {
        this.server = server;
        this.isWhite = isWhite;
    }

    @Override
    public String eval(String input) {
        return "";
    }
}
