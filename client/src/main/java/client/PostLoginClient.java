package client;

public class PostLoginClient implements Client{
    private final ServerFacade server;

    public PostLoginClient(ServerFacade server) {
        this.server = server;
    }

    @Override
    public String eval(String input) {
        return "";
    }
}
