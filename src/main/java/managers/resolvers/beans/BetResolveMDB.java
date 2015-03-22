package main.java.managers.resolvers.beans;

import main.java.managers.bets.BetManager;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.naming.NamingException;
import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(
        mappedName="jms/javaee7/BetActionQueue",
        activationConfig =
                {
                        @ActivationConfigProperty(propertyName = "destinationType",
                                propertyValue = "javax.jms.Queue"),
                        @ActivationConfigProperty(propertyName = "destination",
                                propertyValue = "BetActionQueue")
                })
public class BetResolveMDB implements MessageListener {

    private Logger log = Logger.getAnonymousLogger();

    @EJB
    private BetManager betManager;

    public void onMessage(Message msg) {

        TextMessage tms = (TextMessage) msg;

        try {
            String text = tms.getText();
            log.log(Level.SEVERE, "BET POST RESOLVING=" + tms.getText());
            String[] sp = text.split("\\:");
            String msgQualifier = sp[0];
            Long betId = Long.parseLong(sp[1]);
            String action = sp[2];

            if (msgQualifier.equals("UserBet")) {
                switch (action) {
                    case "resolved":
                        betManager.resolveBet(betId);
                        log.log(Level.INFO, "USER BET FULLY RESOLVED->" + betId);
                        break;
                    case "hold":
                        betManager.activateBet(betId);
                        log.log(Level.INFO, "USER BET ACTIVATED->" + betId);
                }
            }
        } catch (JMSException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            log.log(Level.SEVERE, e.getMessage() + "\r\n" + e.getStackTrace().toString());
        }
    }
}
