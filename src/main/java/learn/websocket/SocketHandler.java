package learn.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class SocketHandler extends TextWebSocketHandler {
    private final ArrayList<String> state = new ArrayList<>();
    private final HashMap<String, WebSocketSession> sessions = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.printf("Session established: %s%n", session.getId());
        sessions.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        // this is just text, not JSON
        state.add(message.getPayload());

        String json;
        try {
            json = mapper.writeValueAsString(state);
        } catch (JsonProcessingException ex) {
            System.out.println("WS Json Failure:" + ex.getMessage());
            return;
        }
        TextMessage msg = new TextMessage(json);

        for (WebSocketSession s : sessions.values()) {
            s.sendMessage(msg);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.printf("Transport err%nSession ID:%s%n%s%n", session.getId(), exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.printf("Connection closed: %s, %s%n", session.getId(), status);
        sessions.remove(session.getId());
        session.close();
    }

    public void broadcast(String message) throws IOException {
        TextMessage msg = new TextMessage(message);

        for (WebSocketSession s : sessions.values()) {
            s.sendMessage(msg);
        }
    }
}
