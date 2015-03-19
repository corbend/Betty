package main.java.managers.loggers;

import main.java.managers.loggers.interfaces.SystemEventLogger;

import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class ConcreteLogger implements SystemEventLogger {

    @EJB
    private SysLogManager sysLogManager;

    @AroundInvoke
    public Object interceptCall(InvocationContext ic) throws Exception {

        sysLogManager.log(ic);

        try {
            return ic.proceed();
        } finally {

        }
    };

    public void log(InvocationContext ic) {};
}
