package org.apache.avalon.aspect;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;

/**
 * Supplies a ServiceManager.
 *
 * <p>Applies to: Any Serviceable Handler
 */
public class ServiceManagerAspect extends AbstractAspect {
    
    public void applyToHandler (String key, Handler handler) {
        if (handler instanceof Serviceable) {
            try {
                ((Serviceable) handler).service (new ServiceManagerImpl (key));
            } catch (Exception e) {
                e.printStackTrace ();
            }
        }
    }
    
    public void apply (String key, Object object) {
    }
    
    private class ServiceManagerImpl implements ServiceManager {
        
        private final String accessor;
        
        public ServiceManagerImpl (String accessor) {
            this.accessor = accessor;
        }
        
        public boolean hasService (String key) {
            Handler h = getKernel ().getHandler (key);
            return h != null;
        }
        
        public Object lookup (String key) {
            Handler h = getKernel ().getHandler (key);
            return h.get (accessor);
        }
        
        public void release (Object o) {
            // Do something smart here...
        }
    }
}