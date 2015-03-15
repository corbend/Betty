package main.java.controllers;

import main.java.managers.messages.AccountMessage;
import main.java.managers.users.UserEJB;
import main.java.models.users.User;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.*;
import java.io.Serializable;
import java.util.logging.Logger;

@ManagedBean
//@Stateless
@RequestScoped //dont use RequestScoped from EJB package
public class UserRegistrationCtrl implements Serializable {

    @Inject
    @JMSConnectionFactory("jms/javaee7/ConnectionFactory")
    private JMSContext context;
    @Resource(lookup = "jms/javaee7/AccountActionQueue")
    private Queue accountQueue;

    private User user = new User();

    private Logger log = Logger.getLogger(UserRegistrationCtrl.class.getName());

    @EJB
    private UserEJB userEJB;

    //getters & setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String doRegistration() {
        return "authorization/registration.xhtml";
    }

    private void createDefaultAccount(User user) throws JMSException {

        //создание аккаунта по умолчанию при первом входе пользователя в систему
        ObjectMessage msg = context.createObjectMessage();
        AccountMessage accMsg = new AccountMessage();
        String userName = user.getLogin();
        accMsg.setAction("CREATE_DEFAULT");
        accMsg.setEntityId(user.getLogin());
        msg.setObject(accMsg);

        context.createProducer().send(accountQueue, msg);
        JMSConsumer consumer = context.createConsumer(accountQueue);

        while (true) {
            ObjectMessage inputMsg = (ObjectMessage) consumer.receive();
            AccountMessage inputAccMsg = (AccountMessage) inputMsg.getObject();

            String verifiedId = inputAccMsg.getEntityId();

            if (verifiedId == null) {
                throw new NoSuchFieldError("Verification failed!");
            }

            if (verifiedId.equals(userName)) {
                return;
            }
        }
    }

    public String doRegisterUser() throws JMSException {

        String login = "admin";
        String email = "admin@domain.com";
        String telephone = "12345678901";

        if (user.getLogin() == null || user.getLogin().equals("")) {
            user.setLogin(login);
        }

        if (user.getEmail() == null || user.getEmail().equals("")) {
            user.setEmail(email);
        }

        if (user.getTelephone() == null || user.getTelephone().equals("")) {
            user.setTelephone(telephone);
        }

        userEJB.addNewUser(user.getLogin(), user.getPassword(), user.getEmail(), user.getTelephone());
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, user.getLogin(),
                        "User" + user.getLogin() + " was created with id=" + user.getLogin()));

        createDefaultAccount(user);

        return "userRedirect.xhtml";
    }

    public String cancelRegistration() {
        return "login.xhtml";
    }
}
