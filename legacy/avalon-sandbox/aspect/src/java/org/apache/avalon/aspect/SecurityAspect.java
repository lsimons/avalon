package org.apache.avalon.aspect;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.NullLogger;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;

/**
 * Supplies a very simple security manager. (Allow everything, just log it.)
 *
 * <p>Applies to: Any LifecycleInterceptable Handler
 */
public class SecurityAspect extends AbstractAspect implements LogEnabled {
    
    private Logger logger = new NullLogger ();
    
    public void applyToHandler (String key, Handler handler) {
        if (handler instanceof LifecycleInterceptable) {
            ((LifecycleInterceptable) handler).addLifecycleInterceptor (new SecurityInterceptor (key));
        }
    }
    
    public void apply (String key, Object object) {
    }
    
    public void enableLogging (Logger logger) {
        this.logger = logger;
    }
    
    private class SecurityInterceptor implements LifecycleInterceptor {
        
        private final String accessed;
        
        public SecurityInterceptor (String accessed) {
            this.accessed = accessed;
        }
        
        public Object interceptCreation (Object instance) {
            return instance;
        }
        
        public Object interceptAccess (String accessor, Object instance) {
            try {
                Class[] proxyInterfaces = instance.getClass ().getInterfaces ();
                InvocationHandler securityHandler = new SecurityInvocationHandler (accessor, accessed, instance);
                
                return Proxy.newProxyInstance(instance.getClass ().getClassLoader(),
                    proxyInterfaces,
                    securityHandler);
            } catch (Exception e) {
                logger.error ("Unable to proxy: " + e, e);
                return instance;
            }
        }
    }
    
    private class SecurityInvocationHandler implements InvocationHandler {
        
        private final String accessor;
        private final String accessed;
        private final Object instance;
        
        public SecurityInvocationHandler (String accessor, String accessed, Object instance) {
            this.accessor = accessor;
            this.accessed = accessed;
            this.instance = instance;
        }
        
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            logger.warn ("'" + accessor + "' is invoking method " + method.getName () + " in '" + accessed + "'");
            return method.invoke (instance, args);
        }
    }
}