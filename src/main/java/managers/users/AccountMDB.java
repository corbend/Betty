package main.java.managers.users;

import main.java.managers.bets.BetManager;
import main.java.managers.messages.AccountMessage;

import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.*;

//@JMSConnectionFactoryDefinition(name = "jms/javaee7/ConnectionFactory",
        //className = "javax.jms.ConnectionFactory")
//@JMSDestinationDefinition(name = "jms/javaee7/AccountTopic",
        //className = "javax.jms.Topic", interfaceName = "javax.jms.Destination")
//@MessageDriven(name="jms/javaee7/AccountTopic")
public class AccountMDB {//implements MessageListener {

//    @EJB
//    private BetManager betManager;
//
//    @Override
//    public void onMessage(Message msg) {
//
//        ObjectMessage objectMsg = (ObjectMessage) msg;
//
//        try {
//            AccountMessage m = (AccountMessage) objectMsg.getObject();
//
//            if (m.getAction() == "ACCOUNT_DECREMENT_OK" && m.getNextAction() == "BET_ACTIVATE") {
//                betManager.activateBet(m.getEntityId());
//            }
//
//        } catch(JMSException e) {
//            e.printStackTrace();
//        }
//
//    }
}
