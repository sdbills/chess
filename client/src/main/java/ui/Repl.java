package ui;

import client.Client;
import client.PostLoginClient;
import client.PreLoginClient;
import client.ServerFacade;

import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final PreLoginClient preClient;
    private final ServerFacade server;
    private Client currClient;

    public Repl(int port) {
        server = new ServerFacade(port);
        preClient = new PreLoginClient(server);
        currClient = preClient;
    }

    public void run() {
        System.out.println(BLACK_KING + "Welcome to Chess! Enter 'help' for to get started." + BLACK_QUEEN);

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.print(">>> ");
            String input = scanner.nextLine();

            result = currClient.eval(input);
            System.out.println(result);
            if (Objects.equals(result, "REGISTERED") || Objects.equals(result, "LOGGED IN")) {
                currClient = new PostLoginClient(server);
            }
        }
    }



}
