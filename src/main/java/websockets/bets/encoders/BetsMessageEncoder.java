package main.java.websockets.bets.encoders;

import main.java.websockets.bets.messages.BetsMessage;
import main.java.websockets.games.messages.GameEventsMessage;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class BetsMessageEncoder implements Encoder.Text<BetsMessage> {

    @Override
    public String encode(BetsMessage object) throws EncodeException {
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
