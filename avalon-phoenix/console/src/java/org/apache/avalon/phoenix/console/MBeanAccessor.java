package org.apache.avalon.phoenix.console;

import java.util.ArrayList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ObjectInstance;
import org.apache.jmx.adaptor.RMIAdaptor;

/**
 *  This is a small utility class to allow easy access to mbean attributes.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class MBeanAccessor
{
    private final RMIAdaptor m_mBeanServer;
    private final ObjectName m_objectName;

    private MBeanInfo m_mBeanInfo;
    private ObjectInstance m_objectInstance;

    /** Hold the attribute objects by attribute name */
    private ArrayList m_attributes = new ArrayList();

    /**
     * Create an accessor that treats, mBean attributes as normal attributes.
     * It also adds a special attribute "meta" via which you can get the
     * MBeanInfo.
     *
     * @param mBeanInfo the MBeanInfo to wrap
     */
    public MBeanAccessor( final ObjectName objectName, final RMIAdaptor mBeanServer )
    {
        m_objectName = objectName;
        m_mBeanServer = mBeanServer;
    }

    public String className()
        throws Exception
    {
        return getObjectInstance().getClassName();
    }

    public MBeanInfo info()
        throws Exception
    {
        return getMBeanInfo();
    }

    public ObjectName name()
    {
        return m_objectName;
    }

    /**
     *  Accessor method to get the fields by name.
     *
     *  @param fieldName Name of static field to retrieve
     *
     *  @return The value of the given field.
     */
    public Object get( String fieldName )
        throws Exception
    {
        //We need to force load the MBeanInfo 
        //If it hasn't yet been loaded
        getMBeanInfo();

        if( m_attributes.contains( fieldName ) )
        {
            return m_mBeanServer.getAttribute( m_objectName, fieldName );
        }
        else
        {
            return null;
        }
    }

    private MBeanInfo getMBeanInfo()
        throws Exception
    {
        if( null == m_mBeanInfo )
        {
            m_mBeanInfo = m_mBeanServer.getMBeanInfo( m_objectName );

            final MBeanAttributeInfo[] attributes = m_mBeanInfo.getAttributes();
            if( null != attributes ) 
            {
                for( int i = 0; i < attributes.length; i++ )
                {
                    final MBeanAttributeInfo attribute = attributes[ i ];
                    m_attributes.add( attribute.getName() );
                }
            }
        }

        return m_mBeanInfo;
    }

    private ObjectInstance getObjectInstance()
        throws Exception
    {
        if( null == m_objectInstance )
        {
            m_objectInstance = m_mBeanServer.getObjectInstance( m_objectName );
        }

        return m_objectInstance;
    }
}
