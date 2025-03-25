package ui;

import client.ServerFacade;

import static ui.EscapeSequences.*;

public class Repl {
    private final PreLoginClient preClient;
    private final ServerFacade server;

    public Repl(int port) {
        server = new ServerFacade(port);
        preClient = new PreLoginClient(server);
    }

    public void run() {
        System.out.println(BLACK_KING + "Welcome to Chess! Enter help for to get started." + BLACK_QUEEN);
    }

}
