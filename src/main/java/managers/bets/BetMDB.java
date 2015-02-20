package main.java.managers.bets;

import main.java.managers.messages.BetPutMessage;

import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;


@MessageDriven(mappedName = "jms/BetMDB", name = "BetMDB")
public class BetMDB implements MessageListener {

    @EJB
    private BetManager betManager;

    private static final String RESOLVE = "RESOLVE";
    @Override
    public void onMessage(Message msg) {
        ObjectMessage objectMessage = (ObjectMessage) msg;

        try {
            BetPutMessage putMessage = (BetPutMessage) objectMessage.getObject();
            //проверка на наличие средств
            if (putMessage.getType() == RESOLVE) {
                betManager.resolveBet(putMessage.getBetId());
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
