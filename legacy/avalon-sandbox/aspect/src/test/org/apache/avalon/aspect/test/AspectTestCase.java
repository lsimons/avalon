package org.apache.avalon.aspect.test;

import org.apache.avalon.aspect.*;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import junit.framework.TestCase;

public class AspectTestCase extends TestCase {
    
    public static interface InterfaceA {
        public void methodA () throws Exception;
    }
    
    public static interface InterfaceB{
        public void methodB () throws Exception;
    }
    
    public static class ComponentA extends AbstractLogEnabled implements InterfaceA, Serviceable {
        
        private ServiceManager manager;
        
        public void service (ServiceManager manager) {
            this.manager = manager;
        }
        
        public void methodA () throws Exception {
            getLogger ().info ("methodA called");
            
            InterfaceB b = (InterfaceB) manager.lookup ("b");
            b.methodB ();
            manager.release (b);
            
            getLogger ().info ("methodA returning");
        }
    } 
    
    public static class ComponentB extends AbstractLogEnabled implements InterfaceB {
        public void methodB () throws Exception {
            getLogger ().info ("methodB called & returning");
        }
    } 
    
    public void testAll () throws Exception {
        Kernel kernel = new DefaultKernel ();
        kernel.addAspect ("logging", new LoggingAspect (new ConsoleLogger ()));
        kernel.addAspect ("servicemanager", new ServiceManagerAspect ());
        kernel.addAspect ("security", new SecurityAspect ());
        
        kernel.addHandler ("a", new Avalon4ComponentHandler (ComponentA.class));
        kernel.addHandler ("b", new Avalon4ComponentHandler (ComponentB.class));
        
        Handler aHandler = kernel.getHandler ("a");
        InterfaceA a = (InterfaceA) aHandler.get ("AspectTestCase");
        a.methodA ();
        aHandler.release ("AspectTestCase", a);
    }
}