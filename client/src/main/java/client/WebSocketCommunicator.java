package client;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.messages.Notification;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketCommunicator extends Endpoint {

    Session session;

    public WebSocketCommunicator(String serverURL, NotificationHandler notificationHandler) throws ResponseException {
        try {
            String url = serverURL.replace("http", "ws");
            URI uri = new URI(url + "/connect");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler((MessageHandler.Whole<String>) s -> {
                Notification notification = new Gson().fromJson(s, Notification.class);
                notificationHandler.notify(notification);
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
