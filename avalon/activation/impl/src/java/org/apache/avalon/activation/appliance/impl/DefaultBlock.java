/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

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

package org.apache.avalon.activation.appliance.impl;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Hashtable;
import java.util.ArrayList;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.avalon.activation.appliance.Block;
import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.ApplianceException;
import org.apache.avalon.activation.appliance.ApplianceRepository;
import org.apache.avalon.activation.appliance.AssemblyException;
import org.apache.avalon.activation.appliance.DependencyGraph;
import org.apache.avalon.activation.appliance.Composite;
import org.apache.avalon.activation.appliance.Engine;
import org.apache.avalon.activation.appliance.ServiceContext;
import org.apache.avalon.activation.appliance.NoProviderDefinitionException;
import org.apache.avalon.activation.appliance.DeploymentException;
import org.apache.avalon.activation.appliance.UnknownServiceException;
import org.apache.avalon.composition.data.ContextDirective;
import org.apache.avalon.composition.data.CategoriesDirective;
import org.apache.avalon.composition.data.ServiceDirective;
import org.apache.avalon.composition.model.Model;
import org.apache.avalon.composition.model.StageModel;
import org.apache.avalon.composition.model.DependencyModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.util.ExceptionHelper;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.Version;
import org.apache.avalon.meta.info.ExtensionDescriptor;
import org.apache.avalon.meta.info.InfoDescriptor;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.ServiceDescriptor;
import org.apache.avalon.meta.info.ReferenceDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;
import org.apache.avalon.meta.info.Type;

/**
 * The DefaultBlock is responsible for the management 
 * of the assembly of the subsidiary appliances, the coordination
 * of the deployment, decommissioning and eventual dissassembly of 
 * contained appliances, and the overall management of a containment 
 * context.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2003/10/17 03:26:28 $
 */
public class DefaultBlock extends AbstractAppliance 
  implements Block, Composite
{
    //-------------------------------------------------------------------
    // static
    //-------------------------------------------------------------------

    /**
     * Create a root containment block.
     * @param context the service context
     * @param model the root containment model
     * @return the appliance
     */
    public static Block createRootBlock( 
      ServiceContext context, ContainmentModel model ) throws Exception
    {
        Logger logger = 
          context.getLoggingManager().getLoggerForCategory( "" );
        ApplianceRepository repository = 
          new DefaultApplianceRepository();
        DependencyGraph graph = new DependencyGraph();
        return new DefaultBlock( 
          logger, model, graph, context, null, repository );
    }

    //-------------------------------------------------------------------
    // immmutable state
    //-------------------------------------------------------------------

    private final DefaultApplianceRepository m_repository;

    private final ServiceContext m_context;

    private final Map m_appliances = new Hashtable();

    private final DependencyGraph m_graph;

    private final Engine m_engine;

    private final ContainmentModel m_model;

    private final DefaultState m_assembly = new DefaultState();

    private final DefaultState m_deployment = new DefaultState();

    private final DefaultState m_self = new DefaultState();

    private final Hashtable m_threads = new Hashtable();

    private final Object m_proxy;

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

   /**
    * Creation of a new containment appliance.
    *
    * @param logger the logging channel to assign
    * @param model the containment model
    * @param graph the dependency graph for this containment model
    * @param context the service context
    * @param engine the parent engine
    * @param repository the parent repository
    * @exception ApplianceException if a block creation error occurs
    */
    DefaultBlock( 
      Logger logger, ContainmentModel model, DependencyGraph graph, 
      ServiceContext context, Engine engine, ApplianceRepository repository )
      throws ApplianceException
    {
        super( logger, model );
        if( graph == null ) throw new NullPointerException( "graph" );
        if( context == null ) throw new NullPointerException( "context" );
        if( repository == null ) throw new NullPointerException( "repository" );

        m_repository = new DefaultApplianceRepository( repository );
        m_model = model;
        m_context = context;
        m_engine = engine;
        m_graph = graph;

        m_self.setEnabled( true );

        Model[] models = model.getModels();
        for( int i=models.length-1; i>-1; i-- )
        {
            Appliance appliance = createAppliance( models[i] );
            m_repository.addAppliance( appliance );
        }
        
        //
        // build the default proxy
        //

        try
        {
            Logger log = getLogger().getChildLogger( "proxy" );
            BlockInvocationHandler handler = new BlockInvocationHandler( log, this );
            Class[] classes = getInterfaceClasses();
            m_proxy = Proxy.newProxyInstance( 
              m_model.getClassLoaderModel().getClassLoader(),
              classes,
              handler );
        }
        catch( Throwable e )
        {
            final String error = 
              "Virtual service establishment failure in block: " + this;
            throw new ApplianceException( error, e );
        }
    }

    //-------------------------------------------------------------------
    // Engine
    //-------------------------------------------------------------------

   /**
    * Return an appliance relative to a supplied dependency model.
    * @param dependency the dependency model
    * @return the appliance
    */
    public Appliance locate( DependencyModel dependency )
      throws Exception
    {
        final String path = dependency.getPath();
        if( path != null )
        {
            return locate( path );
        }
        else
        {
            return locate( dependency.getDependency() );
        }
    }

   /**
    * Return an appliance relative to a supplied dependency descriptor.
    * @param dependency the dependency descriptor
    * @return the appliance
    * @exception NoProviderDefinitionException if no provider an be found
    *    for the supplied dependency
    * @exception Exception if an error occurs
    */
    public Appliance locate( DependencyDescriptor dependency )
      throws Exception
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "resolving provider for: " + dependency );
        }

        // check with registered appliance for an existing solution
        // and if nothing turns up proceed with on-demand appliance
        // creation

        Appliance appliance = m_repository.getAppliance( dependency );
        if( appliance != null ) return appliance;

        //
        // try to establish the model locally and build and appliance
        // from the model and add the appliance to the repository
        //

        Model model = m_model.getModel( dependency );
        if( model != null )
        {
            appliance = createAppliance( model );
            if( appliance instanceof Composite )
            {
                ((Composite)appliance).assemble();
            }
            m_repository.addAppliance( appliance );
            return appliance;
        }

        //
        // try to get an appliance from the parent container
        // if possible - otherwise throw an exception
        //

        if( m_engine != null )
        {
            return m_engine.locate( dependency );
        }
        else
        {
            throw new NoProviderDefinitionException( 
              "Unable to resolve dependency: " + dependency );
        }
    }

   /**
    * Return an appliance relative to a supplied stage model.
    * @param stage the stage model
    * @return the appliance
    */
    public Appliance locate( StageModel stage )
      throws Exception
    {
        final String path = stage.getPath();
        if( path != null )
        {
            return locate( path );
        }
        else
        {
            return locate( stage.getStage() );
        }
    }

   /**
    * Return an appliance relative to a supplied stage descriptor.
    * @param stage the stage descriptor
    * @return the appliance
    * @exception NoProviderDefinitionException if no provider an be found
    *    for the supplied stage
    * @exception Exception if an error occurs
    */
    public Appliance locate( StageDescriptor stage )
      throws NoProviderDefinitionException, Exception
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "resolving stage provider for: " + stage );
        }

        // check with registered appliance for an existing solution
        // and if nothing turns up proceed with on-demand appliance
        // creation

        Appliance appliance = m_repository.getAppliance( stage );
        if( appliance != null ) return appliance;

        //
        // try to establish the model locally 
        //

        Model model = m_model.getModel( stage );
        if( model != null )
        {
            appliance = createAppliance( model );
            if( appliance instanceof Composite )
            {
                ((Composite)appliance).assemble();
            }
            m_repository.addAppliance( appliance );
            return appliance;
        }

        //
        // try to get an appliance from the parent container
        //

        if( m_engine != null )
        {
            return m_engine.locate( stage );
        }
        else
        {
            throw new NoProviderDefinitionException( 
              "Unable to resolve stage handler for: " + stage );
        }
    }

   /**
    * Return an appliance relative to a specific path.
    * @param source the appliance path
    * @return the appliance
    * @exception Exception if an error occurs
    */
    public Appliance locate( String source )
      throws Exception
    {
        String path = source;
        if(( source.length() > 1 ) && source.endsWith( "/" ))
        {
            path = source.substring( 0, source.length() -1 );
        }

        final String base = getModel().getQualifiedName();

        if( path.equals( base ) )
        {
            return this;
        }

        if( path.startsWith( base ) )
        {
            //
            // its a local appliance
            //

            String name = path.substring( base.length() );
            int j = name.indexOf( "/" );
            if( j == -1 )
            {
                return m_repository.getLocalAppliance( name );
            }
            else if( j == 0 )
            {
                return m_repository.getLocalAppliance( name.substring( 1 ) );
            }
            else
            {
                final String root = name.substring( 0, j );
                Appliance child = m_repository.getLocalAppliance( root );
                if( child instanceof Engine )
                {
                    return ((Engine)child).locate( path );
                }
                else
                {
                    final String error = "Not a container: " + path;
                    throw new ApplianceException( error );
                }
            }
        }
        else
        {
            //
            // its either a relative path or a absolute path resolvable by
            // a parent
            //

            if( base.startsWith( path ) )
            {
                //
                // its a parent reference
                //

                if( m_engine != null )
                {
                    return m_engine.locate( path );
                }
                else
                {
                    final String error = "Invalid absolute reference: [" + path + "]";
                    throw new IllegalArgumentException( error );
                }
            }
            else
            {
                //
                // its a relative reference
                //

                return m_repository.getLocalAppliance( path );
            }
        }
    }

    //-------------------------------------------------------------------
    // Composite
    //-------------------------------------------------------------------

    /**
     * Returns the assembled state of the appliance.
     * @return true if this appliance is assembled
     */
    public boolean isAssembled()
    {
        return m_assembly.isEnabled();
    }

    /**
     * Assemble the appliance.
     * @exception ApplianceException if an error occurs during 
     *     appliance assembly
     */
    public void assemble() throws AssemblyException
    {
        synchronized( m_assembly )
        {
            if( isAssembled() )
            {
                return;
            }
            getLogger().debug( "assembly phase" );
            Appliance[] appliances = m_repository.getAppliances();
            for( int i=0; i<appliances.length; i++ )
            {
                Appliance appliance = appliances[i];
                if( appliance instanceof Composite )
                {
                    ((Composite)appliance).assemble();
                }
            }
            m_assembly.setEnabled( true );
        }
    }

    /**
     * Disassemble the appliance.
     */
    public void disassemble()
    {
        synchronized( m_assembly )
        {
            if( !isAssembled() )
            {
                return;
            }
            getLogger().debug( "dissassembly phase" );
            Appliance[] appliances = m_repository.getAppliances();
            for( int i=0; i<appliances.length; i++ )
            {
                Appliance appliance = appliances[i];
                if( appliance instanceof Composite )
                {
                    ((Composite)appliance).disassemble();
                }
            }
            m_assembly.setEnabled( false );
        }
    }

   /**
    * Returns the set of appliances instances that provide
    * services to the set of appliances managed by this container.
    *
    * @return an empty set of consumed appliance instances 
    */
    public Appliance[] getProviders()
    {
        if( !isAssembled() )
        {
            throw new IllegalStateException( "assembly" );
        }

        ArrayList list = new ArrayList();
        Appliance[] appliances = m_repository.getAppliances();
        for( int i=0; i<appliances.length; i++ )
        {
            Appliance appliance = appliances[i];
            if( appliance instanceof Composite )
            {
                Appliance[] providers = ((Composite)appliance).getProviders();
                for( int j=0; j<providers.length; j++ )
                {
                    Appliance provider = providers[j];
                    final String path = 
                      provider.getModel().getPath();
                    if( !path.startsWith( m_model.getPartition() ) )
                    {
                        list.add( providers[j] );
                    }
                }
            }
        }
        return (Appliance[]) list.toArray( new Appliance[0] );
    }

    //-------------------------------------------------------------------
    // Deployable
    //-------------------------------------------------------------------

   /**
    * Deploy the appliance. If the deployment policy is startup
    * an initial instance of a component will be deployed.
    *
    * @exception Exception if a deployment error occurs
    */
    public void deploy() throws Exception
    {
        if( !isAssembled() )
        {
            throw new IllegalStateException( "assembly" );
        }

        synchronized( m_deployment )
        {
            if( m_deployment.isEnabled() )
            {
                return;
            }
        
            Appliance[] appliances = getLocalStartupSequence();
            if( getLogger().isDebugEnabled() )
            {
                String message = listAppliances( "deployment: ", appliances );
                getLogger().debug( message );
            }

            for( int i=0; i<appliances.length; i++ )
            {
                Appliance appliance = appliances[i];
                if( appliance instanceof Block )
                {
                    Block block = (Block) appliance;
                    BlockThread thread = new BlockThread( block );
                    m_threads.put( block, thread );
                    thread.start();

                    //
                    // wait for the thread to complete startup
                    //

                    while( !thread.started() )
                    {
                        try
                        {
                            Thread.currentThread().sleep( 300 );
                        }
                        catch( Throwable e )
                        {
                            // wakeup
                        }
                    }
 
                    //
                    // check for any errors raised during startup
                    //

                    if( thread.getError() != null )
                    {
                        final String error =
                          "Composite deployment failure in block: [" + block + "]";
                        throw new DeploymentException( error, thread.getError() );  
                    }
                }
                else
                {
                    appliances[i].deploy();
                }
            }
            m_deployment.setEnabled( true );
        }
    }

   /**
    * Decommission the block.  Under the decommissioning phase, 
    * all active components will be taken down.
    */
    public void decommission()
    {
        if( !isAssembled() ) return;
        synchronized( m_deployment )
        {
            if( !m_deployment.isEnabled() ) return;

            Appliance[] appliances = getLocalShutdownSequence();
            if( getLogger().isDebugEnabled() )
            {
                String message = listAppliances( "decommissioning: ", appliances );
                getLogger().debug( message );
            }

            for( int i=0; i<appliances.length; i++ )
            {
                Appliance appliance = appliances[i];
                if( appliance instanceof Block )
                {
                    BlockThread thread = 
                      (BlockThread) m_threads.get( appliance );
                    thread.decommission();

                    //
                    // wait for the thread to complete decommissioning
                    //

                    while( !thread.stopped() )
                    {
                        try
                        {
                            Thread.currentThread().sleep( 300 );
                        }
                        catch( Throwable e )
                        {
                            // wakeup
                        }
                    }
                }
                else
                {
                    appliance.decommission();
                }
            }

            m_deployment.setEnabled( false );
        }
    }

    //-------------------------------------------------------------------
    // Home
    //-------------------------------------------------------------------

    /**
     * Resolve a object to a value.
     *
     * @return the resolved object
     * @throws Exception if an error occurs
     */
    public Object resolve() throws Exception
    {
        return m_proxy;
    }

    /**
     * Release an object
     *
     * @param instance the object to be released
     */
    public void release( Object instance )
    {
        //
        // container proxy is a singleton reference
        //
    }

    //-------------------------------------------------------------------
    // Disposable
    //-------------------------------------------------------------------

    public void dispose()
    {
        synchronized( m_self )
        {
            if( !m_self.isEnabled() ) return;

            getLogger().debug( "disposal phase" );
            Appliance[] appliances = m_repository.getAppliances();
            for( int i=0; i<appliances.length; i++ )
            {
                Appliance appliance = appliances[i];
                if( appliance instanceof Disposable )
                {
                    ((Disposable)appliance).dispose();
                }
            }
            m_self.setEnabled( false );
        }
    }

    //-------------------------------------------------------------------
    // implementation
    //-------------------------------------------------------------------

   /**
    * Return a subset of the classic startup sequence in which 
    * all returned appliances are subsidiary appliances to this 
    * apliance.
    *
    * @return the set of subsidiary appliance instances in the 
    *    startup sequence
    */
    private Appliance[] getLocalStartupSequence( )
    {
        Appliance[] appliances = getDependencyGraph().getStartupGraph();
        ArrayList list = new ArrayList();
        for( int i=0; i<appliances.length; i++ )
        {
            Appliance appliance = appliances[i];
            final String path = appliance.getModel().getPath();
            if( path.startsWith( m_model.getPartition() ) )
            {
                list.add( appliance );
            }
        }
        return (Appliance[]) list.toArray( new Appliance[0] );
    }

   /**
    * Return a subset of the classic shutdown sequence in which 
    * all returned appliances are subsidiary appliances to this 
    * apliance.
    *
    * @return the set of subsidiary appliance instances in the 
    *    shutdown sequence
    */
    private Appliance[] getLocalShutdownSequence( )
    {
        Appliance[] appliances = getDependencyGraph().getShutdownGraph();
        ArrayList list = new ArrayList();
        for( int i=0; i<appliances.length; i++ )
        {
            Appliance appliance = appliances[i];
            final String path = appliance.getModel().getPath();
            if( path.startsWith( m_model.getPartition() ) )
            {
                list.add( appliance );
            }
        }
        return (Appliance[]) list.toArray( new Appliance[0] );
    }

   /**
    * Generate a list of appliances as a string.
    * @param header the list title
    * @param appliances the appliances to list
    */
    private String listAppliances( String header, Appliance[] appliances )
    {
        if( appliances.length > 0 )
        {
            boolean flag = true;
            StringBuffer buffer = new StringBuffer( header );
            for( int i=0; i<appliances.length; i++ )
            {
                if( flag )
                {
                    buffer.append( appliances[i] );
                    flag = false;
                }
                else
                {
                    buffer.append( ", " + appliances[i] );
                }
            }
            return buffer.toString();
        }
        else
        {
            return new String( header + " (empty)" );
        }
    }

    /**
     * Return the dependency graph.
     * @return the graph
     */
    private DependencyGraph getDependencyGraph()
    {
        return m_graph;
    }

    /**
     * Create a new appliance.
     * @param model the component model
     * @return the appliance
     */
    public Appliance createAppliance( Model model ) throws ApplianceException
    {
        final String path = model.getPath() + model.getName();
        Appliance appliance = null;

        if( model instanceof DeploymentModel )
        {
            getLogger().debug( "creating appliance: " + path );
            DeploymentModel deployment = (DeploymentModel) model;
            CategoriesDirective categories = deployment.getCategories();
            if( categories != null )
            {
                m_context.getLoggingManager().addCategories( path, categories );
            }
            Logger logger = m_context.getLoggingManager().getLoggerForCategory( path );
            appliance = new DefaultAppliance( logger, m_context, deployment, this );
        }
        else if( model instanceof ContainmentModel )
        {
            getLogger().debug( "creating block: " + path );
            ContainmentModel containment = (ContainmentModel) model;
            CategoriesDirective categories = containment.getCategories();
            if( categories != null )
            {
                m_context.getLoggingManager().addCategories( path, categories );
            }
            Logger logger = m_context.getLoggingManager().getLoggerForCategory( path );
            ApplianceRepository repository = 
              new DefaultApplianceRepository( m_repository );
            appliance = 
              new DefaultBlock( 
                logger, containment, new DependencyGraph( m_graph ), m_context,
                this, repository );
        }
        else
        {
            final String error =
              "Unrecognized model: " + model.getClass().getName();
            throw new IllegalArgumentException( error );
        }

        m_graph.add( appliance );
        return appliance;
    }

    //-------------------------------------------------------------------
    // Object
    //-------------------------------------------------------------------

    public String toString()
    {
        return "block:" + getModel().getQualifiedName();
    }

    //-------------------------------------------------------------------
    // extras
    //-------------------------------------------------------------------

    private Class[] getInterfaceClasses() throws Exception
    {
        ClassLoader loader = m_model.getClassLoaderModel().getClassLoader();
        ArrayList list = new ArrayList();
        ServiceDirective[] services = m_model.getExportDirectives();
        for( int i=0; i<services.length; i++ )
        {
            final ServiceDirective service = services[i];
            final String classname = service.getReference().getClassname();
            try
            {
                Class clazz = loader.loadClass( classname );
                list.add( clazz );
            }
            catch( ClassNotFoundException cnfe )
            {
                final String error = 
                   "Class not found: [" + classname
                   + "] in block [" + this
                   + "] with classloader content: \n";
                StringBuffer buffer = new StringBuffer( error );
                if( loader instanceof URLClassLoader ) 
                {
                    URL[] urls = ((URLClassLoader)loader).getURLs();
                    for( int j=0; j<urls.length; j++ )
                    {
                        buffer.append( "\n  " + urls[j].toString() );
                    }
                }
                String message = buffer.toString();
                throw new ApplianceException( message );
            }
        }
        return (Class[]) list.toArray( new Class[0] );
    }

   /**
    * This makes a dynamic proxy for an object.  The object can be represented
    * by one, some or all of it's interfaces.
    *
    */
    final class BlockInvocationHandler
        implements InvocationHandler
    {
        private final DefaultBlock m_block;
        private final Logger m_logger;

       /**
        * Create a proxy invocation handler.
        *
        * @param block the underlying block implementation
        * @exception if an invocation handler establishment error occurs
        */
        protected BlockInvocationHandler( final Logger logger, final DefaultBlock block )
            throws Exception
        {
            if( block == null ) throw new NullPointerException( "block" );
            m_block = block;
            m_logger = logger;
        }

        /**
         * Invoke the specified method on underlying object.
         * This is called by the proxy object.
         *
         * @param proxy the proxy object
         * @param method the method invoked on proxy object
         * @param args the arguments supplied to method
         * @return the return value of method
         * @throws Throwable if an error occurs
         */
        public Object invoke( final Object proxy,
                final Method method,
                final Object[] args )
                throws Throwable
        {
            if( proxy == null ) throw new NullPointerException( "proxy" );
            if( method == null ) throw new NullPointerException( "method" );

            //
            // if the invocation is against java.lang.Object then
            // delegate the operation to the block
            //

            if( method.getDeclaringClass().equals( java.lang.Object.class ) )
            {
                m_logger.debug( "invocation: " +  method.getName() );
                try
                {
                    return method.invoke( m_block, args );
                }
                catch( InvocationTargetException e )
                {
                    final String error = 
                      "Unexpected delegation error on java.lang.Object";
                    throw new ApplianceException( error, e.getTargetException() );
                }
            }

            //
            // otherwise we are delegating to an implementation component
            //

            ServiceDirective service = 
              m_model.getExportDirective( method.getDeclaringClass() );

            String path = service.getPath();
            Appliance provider = (Appliance) m_block.locate( path );
            m_logger.debug( "delegating: " +  method.getName() );

            //
            // resolve the service object from the appliance
            // and delegate the invocation to that provider
            //

            try
            {
                Object object = provider.resolve();
                return method.invoke( object, args );
            }
            catch( InvocationTargetException e )
            {
                final String error = 
                  "Delegation error raised by provider: " + provider;
                throw new ApplianceException( error, e.getTargetException() );
            }
            catch( Throwable e )
            {
                final String error =
                  "Composite service resolution failure for the class: '" 
                  + method.getDeclaringClass()
                  + "' for operation: '" + method.getName()
                  + "' in appliance: " + m_block;
                throw new ApplianceException( error, e );
            }
        }
    }
}
