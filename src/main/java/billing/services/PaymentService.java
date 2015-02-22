package main.java.billing.services;

import main.java.billing.interfaces.PaymentProvider;

import javax.ejb.MessageDriven;

//@MessageDriven(name="paymentServiceMDB")
public class PaymentService implements PaymentProvider {

    private ProcessingPoint processingPoint;

    public static ProcessingPoint createByName(String name) {
        return new ProcessingPoint(name);
    }

    public void setProcessingPoint(ProcessingPoint ppoint) {
        processingPoint = ppoint;
    }
}
