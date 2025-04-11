package ui;

import chess.ChessGame;
import client.*;
import com.google.gson.Gson;
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
    private GameClient gameClient;


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
        gameClient = new GameClient(server, id, color, this, isPlayer);
        currClient = gameClient;
    }


    private void prompt() {
        String client = RESET_TEXT_COLOR;
        if (currClient.getClass() == PreLoginClient.class) {
            client += "[LOGGED OUT]";
        } else if (currClient.getClass() == PostLoginClient.class) {
            client += "[LOGGED IN]";
        } else if (currClient.getClass() == GameClient.class) {
            client += "[GAME]";
        }
        System.out.print(client + ">>>");
    }

    @Override
    public void notify(String message) {
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(
                    new Gson().fromJson(message, NotificationMessage.class).getMessage());
            case ERROR -> displayError((
                    new Gson().fromJson(message, ErrorMessage.class).getMessage()));
            case LOAD_GAME -> loadGame(
                    new Gson().fromJson(message, LoadGameMessage.class).getGame());
        }
        prompt();
    }

    private void displayNotification(String message) {
        System.out.println(SET_TEXT_COLOR_BLUE + message);
    }

    private void displayError(String message) {
        System.out.println(SET_TEXT_COLOR_RED + "Error: " + message);
    }

    private void loadGame(ChessGame game) {
        gameClient.setGame(game);
        System.out.println("\n"  + gameClient.redraw());
    }

}
