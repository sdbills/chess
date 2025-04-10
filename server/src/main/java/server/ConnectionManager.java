package server;

import org.eclipse.jetty.websocket.api.Session;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Integer> connections = new ConcurrentHashMap<>();

    public void add(Session session, Integer gameID) {
        connections.put(session, gameID);
    }

    public void remove(Session session) {
        connections.remove(session);
    }
}
