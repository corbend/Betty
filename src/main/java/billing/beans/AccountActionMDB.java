package main.java.billing.beans;

import main.java.billing.managers.AccountEJB;
import main.java.billing.managers.TransactionEJB;
import main.java.billing.models.Account;
import main.java.managers.messages.AccountMessage;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.inject.Inject;
import javax.jms.*;
import javax.security.auth.login.AccountLockedException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static Logger log = Logger.getLogger(AccountActionMDB.class.getName());

    @EJB
    private AccountEJB accountEJB;

    @EJB
    private TransactionEJB transactionEJB;

    private void returnWithStatus(AccountMessage inputMsg, String status) throws JMSException {

        AccountMessage outputMsg = new AccountMessage();
        outputMsg.setAction(inputMsg.getAction());
        outputMsg.setEntityId(inputMsg.getEntityId());
        outputMsg.setStatus(status);
        ObjectMessage outMsg = context.createObjectMessage();
        outMsg.setObject(outputMsg);
        context.createProducer().send(accountQueue, outMsg);
    }

    @Override
    public void onMessage(Message msg) {
        Long accountId;

        try {

            AccountMessage inputMsg = (AccountMessage) ((ObjectMessage) msg).getObject();
            log.log(Level.INFO, "GET MESSAGE->" + inputMsg.getAction());
            String msgAction = inputMsg.getAction();

            switch(msgAction) {
                case "CREATE_DEFAULT":
                    String userEntityId = inputMsg.getEntityId();
                    accountEJB.createDefaultAccount(userEntityId);
                    msg.acknowledge();
                    returnWithStatus(inputMsg, "OK");
                    break;

                case "ACCOUNT_INC":
                    log.log(Level.INFO, "GET MESSAGE->" + inputMsg.getAction());
                    accountId = inputMsg.getAccountId();
                    Account destAccount;
                    if (accountId == null) {
                        destAccount = accountEJB.getDefaultAccount(inputMsg.getUsername());
                    } else {
                        destAccount = accountEJB.getAccount(inputMsg.getAccountId());
                    }

                    transactionEJB.createChangeTransaction(null, destAccount, inputMsg.getAmount());
                    msg.acknowledge();
                    //returnWithStatus(inputMsg, "OK");
                    break;

                case "ACCOUNT_DEC":
                    log.log(Level.INFO, "GET MESSAGE->" + inputMsg.getAction());
                    accountId = inputMsg.getAccountId();
                    Account srcAccount;
                    if (accountId == null) {
                        srcAccount = accountEJB.getDefaultAccount(inputMsg.getUsername());
                    } else {
                        srcAccount = accountEJB.getAccount(inputMsg.getAccountId());
                    }
                    transactionEJB.createChangeTransaction(srcAccount, null, inputMsg.getAmount());
                    msg.acknowledge();
                    //returnWithStatus(inputMsg, "OK");
                    break;
            }

        } catch(JMSException | AccountLockedException e) {
            e.printStackTrace();
        }

    }
}
