package org.apache.avalon.aspect;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.NullLogger;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;

/**
 * Handles singleton A4 components. Right now, we only support the LogEnabled and Serviceable interfaces.
 */
public class Avalon4ComponentHandler implements Handler, LogEnabled, LifecycleInterceptable, Serviceable {
    
    private final Class componentClass;
    private Object instance = null;
    private Logger logger = new NullLogger ();
    private List lifecycleInterceptors = new ArrayList ();
    private ServiceManager serviceManager;
    
    public Avalon4ComponentHandler (Class componentClass) {
        this.componentClass = componentClass;
    }
    
    public void addLifecycleInterceptor (LifecycleInterceptor interceptor) {
        lifecycleInterceptors.add (interceptor);
    }
    
    public void enableLogging (Logger logger) {
        this.logger = logger;
    }
    
    public synchronized Object get (String accessor) {
        if (instance == null) {
            newInstance ();
        }
        
        Object lookedUpInstance = instance;
        
        Iterator iter = lifecycleInterceptors.iterator ();
        while (iter.hasNext ()) {
            Object interceptedInstance = ((LifecycleInterceptor) iter.next ()).interceptAccess (accessor, lookedUpInstance);
            lookedUpInstance = interceptedInstance;
        }
        
        return lookedUpInstance;
    }
    
    public void release (String accessor, Object o) {
    }
    
    public void service (ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }
    
    public void newInstance () {
        try {
            instance = componentClass.newInstance ();
            if (instance instanceof LogEnabled) {
                ((LogEnabled) instance).enableLogging (logger);
            }
            
            if (instance instanceof Serviceable) {
                ((Serviceable) instance).service (serviceManager);
            }
            
            Iterator iter = lifecycleInterceptors.iterator ();
            while (iter.hasNext ()) {
                Object interceptedInstance = ((LifecycleInterceptor) iter.next ()).interceptCreation (instance);
                instance = interceptedInstance;
            }
            
            // Hold on, why aren't we giving the logger/service manager as part of an
            // interceptCreation call? Why the classic (instance instanceof LogEnabled)?
            //
            // Answer: Because you can't guarantee that the component can accept a logger / serviceManager
            // after it has been created (PicoContainer components, for example, must
            // receive the logger/serviceManager in the constructor). Therefore this can not be 
            // an done by intercepting the creation....
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
}