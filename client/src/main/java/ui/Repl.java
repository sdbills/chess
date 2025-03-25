package ui;

import chess.ChessGame;
import client.*;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final PreLoginClient preClient;
    private final ServerFacade server;
    private PostLoginClient postClient;
    private Client currClient;

    public Repl(String serverURL) {
        server = new ServerFacade(serverURL);
        preClient = new PreLoginClient(server, this);
        currClient = preClient;
    }

    public void run() {
        System.out.println(BLACK_KING + "Welcome to Chess! Enter 'help' for to get started." + BLACK_QUEEN);

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            prompt();

            String input = scanner.nextLine();

            result = currClient.eval(input);
            System.out.println(result);
        }
    }

    public void setPost() {
        if (postClient == null) {
            postClient = new PostLoginClient(server, this);
        }
        currClient = postClient;
    }

    public void setPre() {
        currClient = preClient;
    }

    public void setGame(int id, ChessGame.TeamColor color) {
        new GameClient(server, id, color, this);
    }


    private void prompt() {
        String client = "";
        if (currClient.getClass() == PreLoginClient.class) {
            client = "[LOGGED OUT]";
        } else if (currClient.getClass() == PostLoginClient.class) {
            client = "[LOGGED IN]";
        } else if (currClient.getClass() == GameClient.class) {
            client = "[GAME]";
        }
        System.out.print(client + ">>>");
    }
}
