package client;

public class GameClient implements Client {
    private final ServerFacade server;

    public GameClient(ServerFacade server) {
        this.server = server;
    }

    @Override
    public String eval(String input) {
        return "";
    }
}
