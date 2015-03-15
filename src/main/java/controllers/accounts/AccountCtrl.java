package main.java.controllers.accounts;

import main.java.billing.managers.AccountEJB;
import main.java.billing.models.Account;
import main.java.billing.models.MoneyTransferInfo;
import main.java.billing.models.Person;
import main.java.billing.services.PaymentService;
import main.java.billing.services.ProcessingPoint;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.security.Principal;
import java.util.List;

//ХЗ почему если применять аннотацию Named, то не находит этот бин
@ManagedBean
@Stateless
@RequestScoped
public class AccountCtrl {

    private Person person = new Person();

    private Account activeAccount;
    public Account getActiveAccount() {
        return activeAccount;
    }

    private MoneyTransferInfo transferInfo = new MoneyTransferInfo("", 0.0);
    public MoneyTransferInfo getTransferInfo() {
        return transferInfo;
    }

    public void setTransferInfo(MoneyTransferInfo transferInfo) {
        this.transferInfo = transferInfo;
    }

//    @Resource
//    private SessionContext ctx;

    @Inject
    private PaymentService paymentService;

    @EJB
    private AccountEJB accountEJB;

    @PersistenceContext
    private EntityManager em;

    public String createNewAccount() {
        //TODO - check how to retrieve user Info
//        Principal p = ctx.getCallerPrincipal();
//        String userName = p.getName();
//        person.setExternalId(userName);
        em.persist(person);

        accountEJB.createAccount(person);

        person = new Person();

        return "accountsManagement.xhtml";
    }

    public void deleteAccount() {

        accountEJB.deleteAccount(activeAccount.getId());
    }

    public void increaseBalance() {
        //пополнение баланса
        transferInfo.setAccountId(activeAccount.getId());
        transferInfo.setAction("fill");
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Money Transfer In Progress", "Wait transfer to complete!"));
        try {
            accountEJB.incrementBalance(transferInfo.getAccountId(), transferInfo.getCurrentAmmount());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Money Transfer Success", "Transaction completed!"));
        } catch (JMSException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Money Transfer", "Error - Transaction aborted!"));
        }
    }

    public void transferMoney() {
        //вывод средств в платежные системы
        transferInfo.setAccountId(activeAccount.getId());
        transferInfo.setAction("transfer");

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Money Transfer In Progress", "Wait transfer to complete!"));
        try {
            accountEJB.decrementBalance(transferInfo.getAccountId(), transferInfo.getCurrentAmmount());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Money Transfer Success", "Transaction completed!"));
        } catch (JMSException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Money Transfer Error", "Transaction aborted!"));
        } catch (AccountEJB.NotEnoughFundsException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Money Transfer Error", "Not enough funds to proceed!"));
        }
    }

    public void setActivePaymentService() {
        //выбор активного платежного сервиса
        ProcessingPoint ppoint = paymentService.createByName(transferInfo.getPaymentServiceName());
        paymentService.setProcessingPoint(ppoint);
    }

    public void setActiveAccount(Account acc) {
        activeAccount = acc;
    }

    public List<Account> getAccounts() {
        //TODO - добавить фильтрация по пользователю
        return accountEJB.getAccounts();
    }
}
