package learn.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;

@RestController
public class TheController {

    private final ArrayList<String> state = new ArrayList<>();

    private final ObjectMapper mapper = new ObjectMapper();
    private final SocketHandler socketHandler;

    public TheController(SocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    @PostMapping("/api/message")
    public ResponseEntity<?> post(@RequestBody Payload payload) throws IOException {

        state.add(payload.getMessage());

        String json = null;
        try {
            json = mapper.writeValueAsString(state);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        socketHandler.broadcast(json);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
