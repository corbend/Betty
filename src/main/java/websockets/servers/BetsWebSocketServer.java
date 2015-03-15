package main.java.websockets.servers;

import main.java.managers.bets.BetManager;
import main.java.managers.bets.LiveBetsManager;
import main.java.models.bets.LiveBet;
import main.java.websockets.bets.BetInfo;
import main.java.websockets.bets.messages.BetsMessage;
import main.java.websockets.games.decoders.GameMessageDecoder;
import main.java.websockets.games.encoders.GameMessageEncoder;
import main.java.websockets.games.messages.GameEventsMessage;

import javax.ejb.EJB;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/bets",
        decoders = {
                GameMessageDecoder.class,
        },
        encoders = {
                GameMessageEncoder.class
        }
)

public class BetsWebSocketServer {

    @EJB
    private BetManager betManager;

    @EJB
    private LiveBetsManager liveBetManager;

    @OnClose
    public void handleClosedConnection(Session session) {
        //stub
    }

    @OnMessage
    public void handleMessage(BetsMessage message, Session session){

        switch (message.getType()){

            case BetsMessage.LOGOUT_REQ:

                handleClosedConnection(session);
                break;

            case BetsMessage.BET_PUT:

                BetInfo betInfo = new BetInfo((String) message.getData());
                LiveBet liveBet = liveBetManager.get(betInfo.getLiveBetId());

                betManager.putBet(betInfo.getUserId(), liveBet, betInfo.getAmmount());

                BetsMessage outMsg = new BetsMessage.BetSuccessMessage(betInfo.toString());

                try {
                    session.getBasicRemote().sendObject(outMsg);
                } catch(IOException | EncodeException e) {

                }

                break;
        }

    }
}
