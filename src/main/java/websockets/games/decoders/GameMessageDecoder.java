package main.java.websockets.games.decoders;

import main.java.websockets.games.messages.GameEventsMessage;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class GameMessageDecoder implements Decoder.Text<GameEventsMessage> {

    @Override
    public GameEventsMessage decode(String s) {
        String[] tokens = s.split(":");

        return new GameEventsMessage(tokens[0], tokens[1]);
    }

    @Override
    public boolean willDecode(String s) {
        return s.startsWith(GameEventsMessage.GAME_START) ||
                s.startsWith(GameEventsMessage.GAME_END) ||
                s.startsWith(GameEventsMessage.GAME_ADD);
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
