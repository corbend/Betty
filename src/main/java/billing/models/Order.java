package main.java.billing.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="orders")
public class Order {
    //модель заказа на вывод средств

    @Id
    @GeneratedValue
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date dateToResolve;
    private Account account;
    private Double amount;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getDateToResolve() {
        return dateToResolve;
    }

    public void setDateToResolve(Date dateToResolve) {
        this.dateToResolve = dateToResolve;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

}
