package main.java.websockets.games.messages;

import main.java.websockets.WebSocketMessage;

public class GameEventsMessage extends WebSocketMessage {

    public static final String GAME_ADD = "game_add";
    public static final String GAME_START = "game_start";
    public static final String GAME_END = "game_end";

    public GameEventsMessage(String type, Object data) {
        super(type, data);
    }

    public static class GameAddMessage extends GameEventsMessage {
        public GameAddMessage(Object data) {
            super(GameEventsMessage.GAME_ADD, data);
        }
    }

    public static class GameStartMessage extends GameEventsMessage {
        public GameStartMessage(Object data) {
            super(GameEventsMessage.GAME_START, data);
        }
    }

    public static class GameEndMessage extends GameEventsMessage {
        public GameEndMessage(Object data) {
            super(GameEventsMessage.GAME_END, data);
        }
    }
}
