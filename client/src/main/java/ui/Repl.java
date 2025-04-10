package ui;

import chess.ChessGame;
import client.*;
import exception.ResponseException;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final PreLoginClient preClient;
    private final ServerFacade server;
    private PostLoginClient postClient;
    private Client currClient;


    public Repl(String serverURL) {
        server = new ServerFacade(serverURL, this);
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

    public void setGame(int id, ChessGame.TeamColor color, boolean isPlayer) throws ResponseException {
        currClient = new GameClient(server, id, color, this, isPlayer);
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

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
            case ERROR -> displayError(((ErrorMessage) message).getMessage());
            case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGame());
        }
        prompt();
    }

    private void displayNotification(String message) {
    }

    private void displayError(String message) {
    }

    private void loadGame(ChessGame game) {
    }

}
