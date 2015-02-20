package main.java.controllers;

import main.java.managers.users.UserEJB;
import main.java.models.users.User;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.util.logging.Logger;

@Named(value="userRegistrationCtrl")
@RequestScoped //dont use RequestScoped from EJB package
@Stateless
public class UserRegistrationCtrl {

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

    public String doRegisterUser() {

        log.fine("Prepare to create user!" + user.getLogin());
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
        log.fine("User is created!");
        return "account.xhtml";
    }

    public String cancelRegistration() {
        return "login.xhtml";
    }
}
