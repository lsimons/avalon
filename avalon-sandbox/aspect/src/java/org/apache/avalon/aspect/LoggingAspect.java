package org.apache.avalon.aspect;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.LogEnabled;

/**
 * Supplies a Logger.
 *
 * <p>Applies to: Any LogEnabled Handler or Aspect
 */
public class LoggingAspect extends AbstractAspect {
    
    private final Logger logger;
    
    public LoggingAspect (Logger logger) {
        this.logger = logger;
    }
    
    public void apply (String key, Object object) {
        if (object instanceof LogEnabled) {
            ((LogEnabled) object).enableLogging (logger.getChildLogger (key));
        }
    }
}