package org.apache.avalon.phoenix.console;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.InstanceNotFoundException;
import org.apache.jmx.adaptor.RMIAdaptor;

/**
 *  This is a small utility class to interact with an MBeanServer.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class MBeanTool
{
    private final RMIAdaptor m_mBeanServer;

    public MBeanTool( final RMIAdaptor mBeanServer )
    {
        m_mBeanServer = mBeanServer;
    }

    public MBeanAccessor getObject( final String name )
        throws Exception
    {
        return new MBeanAccessor( getObjectName( name ), m_mBeanServer );
    }

    private ObjectName getObjectName( final String name )
        throws Exception
    {
        //Deal with domains et al here
        return new ObjectName( name );
    }
}
