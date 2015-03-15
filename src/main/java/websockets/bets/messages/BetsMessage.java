package main.java.websockets.bets.messages;

import main.java.websockets.WebSocketMessage;

import java.io.Serializable;

public class BetsMessage extends WebSocketMessage {

    public static final String BET_CHANGE = "bet_chg";
    public static final String BET_REMOVE = "bet_rm";
    public static final String BET_ADD = "bet_add";
    public static final String BET_PUT = "bet_put";
    public static final String BET_SUCCESS = "bet_success";

    public BetsMessage(String type, Object data) {
        super(type, data);
    }

    public static class BetChangeMessage extends BetsMessage {
        public BetChangeMessage(Object data) {
            super(BET_CHANGE, data);
        }
    }

    public static class BetRemoveMessage extends BetsMessage {
        public BetRemoveMessage(Object data) {
            super(BET_REMOVE, data);
        }
    }

    public static class BetAddMessage extends BetsMessage {
        public BetAddMessage(Object data) {
            super(BET_ADD, data);
        }
    }

    public static class BetSuccessMessage extends BetsMessage {
        public BetSuccessMessage(Object data) {
            super(BET_SUCCESS, data);
        }
    }
}
