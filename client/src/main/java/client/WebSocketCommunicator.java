package client;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketCommunicator extends Endpoint {

    Session session;

    public WebSocketCommunicator(String serverURL, NotificationHandler notificationHandler) throws ResponseException {
        try {
            String url = serverURL.replace("http", "ws");
            URI uri = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler((MessageHandler.Whole<String>) s -> {
                ServerMessage message = new Gson().fromJson(s, ServerMessage.class);
                notificationHandler.notify(message);
            });

        } catch (DeploymentException | URISyntaxException | IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void send(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}
