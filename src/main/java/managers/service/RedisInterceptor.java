package main.java.managers.service;


import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class RedisInterceptor<T> {

    @AroundInvoke
    public Object withResource(InvocationContext ic) throws Exception {

        RedisManager rm = (RedisManager<T>) ic.getTarget();
        rm.setClient(rm.getPool().getResource());

        try {
            return ic.proceed();
        } finally {
            rm.getPool().returnResource(rm.getClient());
        }
    }
}
