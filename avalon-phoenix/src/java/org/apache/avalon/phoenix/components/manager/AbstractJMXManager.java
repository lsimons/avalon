/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.avalon.phoenix.components.manager;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.modelmbean.ModelMBean;
import javax.management.modelmbean.ModelMBeanInfo;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.phoenix.interfaces.ManagerException;

/**
 * An abstract class via which JMX Managers can extend.
 *
 * @author Peter Donald
 * @author <a href="mailto:Huw@mmlive.com">Huw Roberts</a>
 * @version $Revision: 1.11 $ $Date: 2003/12/05 15:14:36 $
 */
public abstract class AbstractJMXManager
    extends AbstractSystemManager
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( AbstractJMXManager.class );
    private MBeanInfoBuilder topicBuilder;
    private MBeanServer m_mBeanServer;
    private String m_domain = "Phoenix";

    public void initialize()
        throws Exception
    {
        super.initialize();

        final MBeanServer mBeanServer = createMBeanServer();
        setMBeanServer( mBeanServer );

        topicBuilder = new MBeanInfoBuilder();
        setupLogger( topicBuilder );
    }

    public void dispose()
    {
        setMBeanServer( null );

        super.dispose();
    }

    /**
     * Export the object to the particular management medium using
     * the supplied object and interfaces.
     * This needs to be implemented by subclasses.
     *
     * @param name the name of object
     * @param object the object
     * @param interfaces the interfaces
     * @return the exported object
     * @throws ManagerException if an error occurs
     */
    protected Object export( final String name,
                             final Object object,
                             final Class[] interfaces )
        throws ManagerException
    {
        try
        {
            final Target target = createTarget( name, object, interfaces );
            exportTarget( target );
            return target;
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "jmxmanager.error.export.fail", name );
            getLogger().error( message, e );
            throw new ManagerException( message, e );
        }
    }

    /**
     * Stop the exported object from being managed.
     *
     * @param name the name of object
     * @param exportedObject the object return by export
     * @throws ManagerException if an error occurs
     */
    protected void unexport( final String name,
                             final Object exportedObject )
        throws ManagerException
    {
        try
        {
            final Target target = (Target)exportedObject;
            final Set topicNames = target.getTopicNames();
            final Iterator i = topicNames.iterator();

            while( i.hasNext() )
            {
                final ObjectName objectName =
                    createObjectName( name, target.getTopic( (String)i.next() ) );

                getMBeanServer().unregisterMBean( objectName );
            }
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "jmxmanager.error.unexport.fail", name );
            getLogger().error( message, e );
            throw new ManagerException( message, e );
        }
    }

    /**
     * Verify that an interface conforms to the requirements of management medium.
     *
     * @param clazz the interface class
     * @throws ManagerException if verification fails
     */
    protected void verifyInterface( final Class clazz )
        throws ManagerException
    {
        //TODO: check it extends all right things and that it
        //has all the right return types etc. Blocks must have
        //interfaces extending Service (or Manageable)
    }

    protected MBeanServer getMBeanServer()
    {
        return m_mBeanServer;
    }

    protected void setMBeanServer( final MBeanServer mBeanServer )
    {
        m_mBeanServer = mBeanServer;
    }

    protected String getDomain()
    {
        return m_domain;
    }

    protected void setDomain( final String domain )
    {
        m_domain = domain;
    }

    /**
     * Creates a new MBeanServer.
     * The subclass should implement this to create specific MBeanServer.
     */
    protected abstract MBeanServer createMBeanServer()
        throws Exception;

    /**
     * Creates a target that can then be exported for management. A topic is created
     * for each interface and for topics specified in the mxinfo file, if present
     *
     * @param name name of the target
     * @param object managed object
     * @param interfaces interfaces to be exported
     * @return  the management target
     */
    protected Target createTarget( final String name,
                                   final Object object,
                                   final Class[] interfaces )
    {
        final Target target = new Target( name, object );
        try
        {
            topicBuilder.build( target, object.getClass(), interfaces );
        }
        catch( final Exception e )
        {
            getLogger().debug( e.getMessage(), e );
        }

        return target;
    }

    /**
     * Exports the target to the management repository.  This is done by exporting
     * each topic in the target.
     *
     * @param target the management target
     */
    protected void exportTarget( final Target target )
        throws Exception
    {
        // loop through each topic and export it
        final Set topicNames = target.getTopicNames();
        final Iterator i = topicNames.iterator();
        while( i.hasNext() )
        {
            final String topicName = (String)i.next();
            final ModelMBeanInfo topic = target.getTopic( topicName );
            final String targetName = target.getName();
            final Object managedResource = target.getManagedResource();
            Object targetObject = managedResource;
            if( topic.getMBeanDescriptor().getFieldValue( "proxyClassName" ) != null )
            {
                targetObject = createManagementProxy( topic, managedResource );
            }

            // use a proxy adapter class to manage object
            exportTopic( topic,
                         targetObject,
                         targetName );
        }
    }

    /**
     * Exports the topic to the management repository. The name of the topic in the
     * management repository will be the target name + the topic name
     *
     * @param topic the descriptor for the topic
     * @param target to be managed
     * @param targetName the target's name
     */
    protected Object exportTopic( final ModelMBeanInfo topic,
                                  final Object target,
                                  final String targetName )
        throws Exception
    {
        final Object mBean = createMBean( topic, target );
        final ObjectName objectName = createObjectName( targetName, topic );
        getMBeanServer().registerMBean( mBean, objectName );

        // debugging stuff.
        /*
        ModelMBean modelMBean = (ModelMBean)mBean;
        ModelMBeanInfo modelMBeanInfo = (ModelMBeanInfo)modelMBean.getMBeanInfo();
        MBeanAttributeInfo[] attList = modelMBeanInfo.getAttributes();
        for( int i = 0; i < attList.length; i++ )
        {
            ModelMBeanAttributeInfo mbai = (ModelMBeanAttributeInfo)attList[ i ];
            Descriptor d = mbai.getDescriptor();
            String[] fieldNames = d.getFieldNames();
            for( int j = 0; j < fieldNames.length; j++ )
            {
                String fieldName = fieldNames[ j ];
                System.out.println( "Field name = " + fieldName +
                                    " / value = " + d.getFieldValue( fieldName ) +
                                    "::" +mbai.getType() + " value " +
                modelMBean.getAttribute( mbai.getName() ) + " for " + mbai.getName() );
            }
        }
        */

        return mBean;
    }

    /**
     * Create a MBean for specified object.
     * The following policy is used top create the MBean...
     *
     * @param target the object to create MBean for
     * @return the MBean to be exported
     * @throws ManagerException if an error occurs
     */
    private Object createMBean( final ModelMBeanInfo topic,
                                final Object target )
        throws ManagerException
    {
        final String className = topic.getClassName();
        // Load the ModelMBean implementation class
        Class clazz;
        try
        {
            clazz = Class.forName( className );
        }
        catch( Exception e )
        {
            final String message =
                REZ.getString( "jmxmanager.error.mbean.load.class",
                               className );
            getLogger().error( message, e );
            throw new ManagerException( message, e );
        }

        // Create a new ModelMBean instance
        ModelMBean mbean;
        try
        {
            mbean = (ModelMBean)clazz.newInstance();
            mbean.setModelMBeanInfo( topic );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "jmxmanager.error.mbean.instantiate",
                               className );
            getLogger().error( message, e );
            throw new ManagerException( message, e );
        }

        // Set the managed resource (if any)
        try
        {
            if( null != target )
            {
                mbean.setManagedResource( target, "ObjectReference" );
            }
        }
        catch( Exception e )
        {
            final String message =
                REZ.getString( "jmxmanager.error.mbean.set.resource",
                               className );
            getLogger().error( message, e );
            throw new ManagerException( message, e );
        }

        return mbean;
    }

    /**
     * Create JMX name for object.
     *
     * @param name the name of object
     * @return the {@link ObjectName} representing object
     * @throws MalformedObjectNameException if malformed name
     */
    private ObjectName createObjectName( final String name, final ModelMBeanInfo topic )
        throws MalformedObjectNameException
    {
        return new ObjectName( getDomain() + ":" + name + ",topic=" + topic.getDescription() );
    }

    /**
     * Instantiates a proxy management object for the target object
     *
     * this should move out of bridge and into Registry, it isn't specifically for jmx
     */
    private Object createManagementProxy( final ModelMBeanInfo topic,
                                          final Object managedObject )
        throws Exception
    {
        final String proxyClassname = (String)topic.getMBeanDescriptor().getFieldValue( "proxyClassName" );
        final ClassLoader classLoader = managedObject.getClass().getClassLoader();
        final Class proxyClass = classLoader.loadClass( proxyClassname );
        final Class[] argTypes = new Class[]{Object.class};
        final Object[] argValues = new Object[]{managedObject};
        final Constructor constructor = proxyClass.getConstructor( argTypes );
        return constructor.newInstance( argValues );
    }
}
