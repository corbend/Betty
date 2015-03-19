package main.java.managers.loggers;

import main.java.managers.loggers.interfaces.SystemEventLogger;
import main.java.models.sys.Event;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

@Stateless
public class SysLogManager {

    @PersistenceContext
    public EntityManager em;

    public void log(InvocationContext ic) {

        Event ev = new Event(new Date(), ic.getTarget().toString());

        em.persist(ev);
    }
}
