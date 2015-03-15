package main.java.websockets.bets.decoders;


import main.java.websockets.bets.messages.BetsMessage;
import main.java.websockets.games.messages.GameEventsMessage;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class BetsMessageDecoder implements Decoder.Text<BetsMessage> {

    @Override
    public BetsMessage decode(String s) {
        String[] tokens = s.split(":");

        return new BetsMessage(tokens[0], tokens[1]);
    }

    @Override
    public boolean willDecode(String s) {
        return s.startsWith(BetsMessage.BET_ADD) ||
                s.startsWith(BetsMessage.BET_CHANGE) ||
                s.startsWith(BetsMessage.BET_REMOVE) ||
                s.startsWith(BetsMessage.BET_PUT);
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
