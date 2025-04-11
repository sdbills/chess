package server;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Integer> connections = new ConcurrentHashMap<>();

    public void add(Session session, int gameID) {
        connections.put(session, gameID);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcastAll(String message, int gameID) throws IOException {
        var removeList = new ArrayList<Session>();
        for (var c : connections.keySet()) {
            if (c.isOpen()) {
                if (connections.get(c) == gameID) {
                    c.getRemote().sendString(message);
                }
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.remove(c);
        }
    }

    public void broadcastOthers(String message, int gameID, Session exclude) throws IOException {
        var removeList = new ArrayList<Session>();
        for (var c : connections.keySet()) {
            if (c.isOpen()) {
                if (c != exclude && connections.get(c) == gameID) {
                    c.getRemote().sendString(message);
                }
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.remove(c);
        }
    }
}
