package main.java.websockets.games.encoders;

import main.java.websockets.games.messages.GameEventsMessage;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;


public class GameMessageEncoder implements Encoder.Text<GameEventsMessage> {

    @Override
    public String encode(GameEventsMessage object) throws EncodeException {
        return object.toString();
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // do nothing.
    }

    @Override
    public void destroy() {
        // do nothing.
    }
}
