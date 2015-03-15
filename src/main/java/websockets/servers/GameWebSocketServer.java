package main.java.websockets.servers;

import main.java.websockets.games.decoders.GameMessageDecoder;
import main.java.websockets.games.encoders.GameMessageEncoder;
import main.java.websockets.games.messages.GameEventsMessage;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint(value = "/games",
        decoders = {
                GameMessageDecoder.class,
        },
        encoders = {
                GameMessageEncoder.class
        }
)
public class GameWebSocketServer {

    @OnClose
    public void handleClosedConnection(Session session) {
        //stub
    }

    @OnMessage
    public void handleMessage(GameEventsMessage message, Session session){
        String communicationId;

        switch (message.getType()){
            case GameEventsMessage.LOGOUT_REQ:
                handleClosedConnection(session);
                break;
        }

    }
}
