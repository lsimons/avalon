/*
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.jmx.spi;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.modelmbean.ModelMBean;
import javax.management.modelmbean.ModelMBeanInfo;

import org.apache.avalon.composition.model.ComponentModel;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.activity.Disposable;

import org.apache.avalon.jmx.ComponentRegistrationException;
import org.apache.avalon.jmx.util.MBeanInfoBuilder;
import org.apache.avalon.jmx.util.Target;

import org.apache.avalon.util.i18n.ResourceManager;
import org.apache.avalon.util.i18n.Resources;

/**
 * An abstract implementation of a JMX based Component Manager.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractJMXComponentRegistrationManager 
    extends AbstractComponentRegistrationManager 
    implements Disposable
{
    private static final Resources REZ = ResourceManager.getPackageResources(
        AbstractJMXComponentRegistrationManager.class );
        
    private MBeanInfoBuilder topicBuilder;
    private MBeanServer m_mBeanServer;
    private String m_domain = "Merlin";

    public AbstractJMXComponentRegistrationManager( final Logger logger )
      throws Exception
    {
        super( logger );

        final MBeanServer mBeanServer = createMBeanServer();
        setMBeanServer( mBeanServer );
        topicBuilder = new MBeanInfoBuilder();

        if( topicBuilder instanceof LogEnabled )
        {
            ( (LogEnabled)topicBuilder).enableLogging( logger );
        }
    }

    public void dispose()
    {
        setMBeanServer( null );
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
     * @throws ComponentRegistrationException if an error occurs
     */
    protected Object export( final String name, final Object object, final Class[] interfaces ) 
        throws  ComponentRegistrationException
    {
        try
        {
            final Target target = createTarget( name, object, interfaces );
            exportTarget( target );
            return target;
        }
        catch ( final Exception e )
        {
            final String message = REZ.getString( "jmxmanager.error.export.fail", name );
            getLogger().error( message, e );
            throw new ComponentRegistrationException( message, e );
        }
    }

    /**
     * Stop the exported object from being managed.
     *
     * @param name the name of object
     * @param exportedObject the object return by export
     * @throws ComponentRegistrationException if an error occurs
     */
    protected void unexport( final String name, final Object exportedObject ) 
        throws ComponentRegistrationException
    {
        try
        {
            final Target target = ( Target ) exportedObject;
            final Set topicNames = target.getTopicNames();
            final Iterator i = topicNames.iterator();

            while ( i.hasNext() )
            {
                final ObjectName objectName = 
                  createObjectName( name, target.getTopic( ( String ) i.next() ) );
                getMBeanServer().unregisterMBean( objectName );
            }
        }
        catch ( final Exception e )
        {
            final String message = 
              REZ.getString( "jmxmanager.error.unexport.fail", name );
            getLogger().error( message, e );
            throw new ComponentRegistrationException( message, e );
        }
    }

    /**
     * Verify that an interface conforms to the requirements of management medium.
     *
     * @param clazz the interface class
     * @throws ComponentRegistrationException if verification fails
     */
    protected void verifyInterface( final Class clazz ) 
        throws ComponentRegistrationException
    {
        //TODO: check it extends all right things and that it
        //has all the right return types etc. Blocks must have
        //interfaces extending Service (or Manageable)
    }

    protected MBeanServer getMBeanServer()
    {
        return m_mBeanServer;
    }

    protected void setMBeanServer( MBeanServer mBeanServer )
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

    protected Class[] getManagementInterfaces( ComponentModel componentModel )
    {
        final Class objectClass = componentModel.getDeploymentClass();
        final Class[] allInterfaces = objectClass.getInterfaces();
        final Set mbeanInterfaces = new HashSet();
        for ( int i = 0; i < allInterfaces.length; i++ )
        {
            if ( allInterfaces[i].getName().endsWith( "MBean" ) )
            {
                if ( getLogger().isDebugEnabled() )
                {
                    final String message = 
                      REZ.getString( 
                        "jmxmanager.debug.interface",
                        allInterfaces[i].getName() );
                    getLogger().debug( message );
                }

                mbeanInterfaces.add( allInterfaces[i] );
            }
        }

        return ( Class[] ) mbeanInterfaces.toArray( new Class[mbeanInterfaces.size()] );
    }

    protected String getName( ComponentModel componentModel ) 
        throws ComponentRegistrationException
    {
        String[] nameComponents = componentModel.getQualifiedName().split( "/" );
        StringBuffer name = new StringBuffer();
        for ( int i = 0; i < nameComponents.length - 1; i++ )
        {
            String s = nameComponents[i].trim();
            if ( s.length() > 0 )
            {
                name.append( "container=" ).append( nameComponents[i] ).append( ',' );
            }
        }
        name.append( "name=" ).append( nameComponents[nameComponents.length - 1] );
        return name.toString();
    }

    /**
     * Creates a new MBeanServer.
     * The subclass should implement this to create specific MBeanServer.
     * @throws Exception if the MBeanServer cannot be created
     * @return the MBeanServer
     */
    protected abstract MBeanServer createMBeanServer() throws Exception;

    /**
     * Creates a target that can then be exported for management. A topic is created
     * for each interface and for topics specified in the mxinfo file, if present
     *
     * @param name name of the target
     * @param object managed object
     * @param interfaces interfaces to be exported
     * @return  the management target
     */
    protected Target createTarget( final String name, final Object object, Class[] interfaces )
    {
        final Target target = new Target( name, object );
        try
        {
            topicBuilder.build( target, object.getClass(), interfaces );
        }
        catch ( final Exception e )
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
     * @throws Exception if the Target cannot be exported
     */
    protected void exportTarget( final Target target ) 
        throws Exception
    {
        // loop through each topic and export it
        final Set topicNames = target.getTopicNames();
        final Iterator i = topicNames.iterator();
        while ( i.hasNext() )
        {
            final String topicName = ( String ) i.next();
            final ModelMBeanInfo topic = target.getTopic( topicName );
            final String targetName = target.getName();
            final Object managedResource = target.getManagedResource();
            Object targetObject = managedResource;
            if ( topic.getMBeanDescriptor().getFieldValue( "proxyClassName" ) != null )
            {
                targetObject = createManagementProxy( topic, managedResource );
            }

            // use a proxy adapter class to manage object
            exportTopic( topic, targetObject, targetName );
        }
    }

    /**
     * Exports the topic to the management repository. The name of the topic in the
     * management repository will be the target name + the topic name
     *
     * @param topic the descriptor for the topic
     * @param target to be managed
     * @param targetName the target's name
     * @throws Exception if the Topic cannot be exported
     * @return the mBean
     */
    protected Object exportTopic( final ModelMBeanInfo topic, final Object target,
                                  final String targetName ) throws Exception
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
     * @param topic the topic
     * @param target the object to create MBean for
     * @return the MBean to be exported
     * @throws ComponentRegistrationException if an error occurs
     */
    private Object createMBean( final ModelMBeanInfo topic, final Object target ) throws
        ComponentRegistrationException
    {
        final String className = topic.getClassName();
        // Load the ModelMBean implementation class
        Class clazz;
        try
        {
            clazz = Class.forName( className );
        }
        catch ( Exception e )
        {
            final String message = 
              REZ.getString( "jmxmanager.error.mbean.load.class", className );
            getLogger().error( message, e );
            throw new ComponentRegistrationException( message, e );
        }

        // Create a new ModelMBean instance
        ModelMBean mbean = null;
        try
        {
            mbean = ( ModelMBean ) clazz.newInstance();
            mbean.setModelMBeanInfo( topic );
        }
        catch ( final Exception e )
        {
            final String message = 
              REZ.getString( "jmxmanager.error.mbean.instantiate", className );
            getLogger().error( message, e );
            throw new ComponentRegistrationException( message, e );
        }

        // Set the managed resource (if any)
        try
        {
            if ( null != target )
            {
                mbean.setManagedResource( target, "ObjectReference" );
            }
        }
        catch ( Exception e )
        {
            final String message = 
              REZ.getString( "jmxmanager.error.mbean.set.resource", className );
            getLogger().error( message, e );
            throw new ComponentRegistrationException( message, e );
        }

        return mbean;
    }

    /**
     * Create JMX name for object.
     *
     * @param name the name of object
     * @param topic the topic
     * @return the {@link ObjectName} representing object
     * @throws MalformedObjectNameException if malformed name
     */
    private ObjectName createObjectName( final String name, final ModelMBeanInfo topic ) throws
        MalformedObjectNameException
    {
        return new ObjectName( getDomain() + ":" + name + ",topic=" + topic.getDescription() );
    }

    /**
     * Instantiates a proxy management object for the target object
     *
     * this should move out of bridge and into Registry, it isn't specifically for jmx
     *
     * @param topic the topic
     * @param managedObject the managed object
     * @throws Exception if a management proxy cannot be created
     * @return the management proxy
     */
    private Object createManagementProxy( final ModelMBeanInfo topic, final Object managedObject ) 
        throws Exception
    {
        final String proxyClassname = ( String ) topic.getMBeanDescriptor().getFieldValue(
            "proxyClassName" );
        final ClassLoader classLoader = managedObject.getClass().getClassLoader();
        final Class proxyClass = classLoader.loadClass( proxyClassname );
        final Class[] argTypes = new Class[]
                                 {Object.class};
        final Object[] argValues = new Object[]
                                   {managedObject};
        final Constructor constructor = proxyClass.getConstructor( argTypes );
        return constructor.newInstance( argValues );
    }
}
