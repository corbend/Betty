package main.java.controllers;

import main.java.billing.managers.AccountEJB;
import main.java.controllers.accounts.AccountCtrl;

import javax.ejb.Stateless;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@ManagedBean
@Stateless
@RequestScoped
public class UserMenuCtrl implements Serializable {

    @Inject
    private AccountCtrl accountCtrl;

    public String createNewAccount() {

        accountCtrl.createNewAccount();

        return "accountsManagement.xhtml";
    }

    public String goManageAccounts() {
        return "accountsManagement.xhtml";
    }

    public String createNewBets() {
        return "createNewBets.xhtml";
    }

    public String goManageActiveBets() {
        return "activeBetsManagement.xhtml";
    }
}
