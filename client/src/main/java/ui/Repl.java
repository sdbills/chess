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

    public Repl(int port) {
        server = new ServerFacade(port);
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


//            if (Objects.equals(result, "REGISTERED") || Objects.equals(result, "LOGGED IN")) {
//                currClient = new PostLoginClient(server, repl);
//            } else if (Objects.equals(result, "LOGGED OUT")) {
//                currClient = preClient;
//            } else if (Objects.equals(result, "JOINED AS WHITE") || Objects.equals(result, "VIEWING")) {
//                currClient = new GameClient(server, true);
//             }else if (Objects.equals(result, "JOINED AS BLACK")) {
//                new GameClient(server, false);
////                currClient = new GameClient(server, false);
//            }
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
        currClient = new GameClient(server, id, color, this);
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
