
package org.apache.avalon.playground;

import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;

/**
 * Demonstration of meta tags supportign the declaration of a service 
 * export, version, type name, lifestyle, context entries, and the use 
 * of the namespace compating mechanism.
 *
 *
 * @avalon.component name="primary-component" lifestyle="singleton"
 *     version="1.3"
 * @avalon.service type="org.apache.avalon.playground.PrimaryService" 
 *     version="9.8"
 */
public class Primary implements PrimaryService, Contextualizable
{
   /**
    * @avalon.entry key="home" type="java.io.File"
    */
    public void contextualize( Context context ) throws ContextException
    {
    }
}
