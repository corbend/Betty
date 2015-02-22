package main.java.managers;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jms.JMSConnectionFactoryDefinition;
import javax.jms.JMSDestinationDefinition;

@Singleton
@Startup
@JMSConnectionFactoryDefinition(name = "jms/javaee7/ConnectionFactory",
className = "javax.jms.ConnectionFactory")
@JMSDestinationDefinition(name = "jms/javaee7/AccountActionQueue",
        className = "javax.jms.Queue", interfaceName = "javax.jms.Destination")
public class JMSProvider {

}
