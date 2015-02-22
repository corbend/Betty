package main.java.billing.beans;

import main.java.billing.managers.AccountEJB;
import main.java.billing.models.Account;
import main.java.managers.messages.AccountMessage;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.*;

@MessageDriven(
    mappedName="jms/javaee7/AccountActionQueue",
    activationConfig =
    {
            @ActivationConfigProperty(propertyName = "destinationType",
                    propertyValue = "javax.jms.Queue"),
            @ActivationConfigProperty(propertyName = "destination",
                    propertyValue = "AccountActionQueue")
    })
public class AccountActionMDB implements MessageListener {

    @Inject
    @JMSConnectionFactory("jms/javaee7/ConnectionFactory")
    private JMSContext context;
    @Resource(lookup = "jms/javaee7/AccountActionQueue")
    private Queue accountQueue;

    @EJB
    private AccountEJB accountEJB;

    @Override
    public void onMessage(Message msg) {

        try {

            AccountMessage inputMsg = (AccountMessage) ((ObjectMessage) msg).getObject();

            if (inputMsg.getAction().equals("CREATE_DEFAULT")) {
                String userEntityId = inputMsg.getEntityId();
                accountEJB.createDefaultAccount(userEntityId);
                AccountMessage outputMsg = new AccountMessage();
                outputMsg.setAction(inputMsg.getAction());
                outputMsg.setEntityId(userEntityId);
                outputMsg.setStatus("OK");
                ObjectMessage outMsg = context.createObjectMessage();
                outMsg.setObject(outputMsg);
                context.createProducer().send(accountQueue, outMsg);
            }

        } catch(JMSException e) {
            e.printStackTrace();
        }

    }
}
