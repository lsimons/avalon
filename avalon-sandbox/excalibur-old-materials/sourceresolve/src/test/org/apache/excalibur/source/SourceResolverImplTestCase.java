

package org.apache.excalibur.source;

import java.io.File;
import junit.framework.TestCase;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.DefaultServiceManager;
import org.apache.avalon.framework.service.DefaultServiceSelector;
import org.apache.excalibur.source.impl.SourceResolverImpl;
import org.apache.excalibur.source.impl.ResourceSourceFactory;

public class SourceResolverImplTestCase extends TestCase
{

    public SourceResolverImplTestCase( )
    {
        this( "source" );
    }

    public SourceResolverImplTestCase( String name )
    {
        super( name );
    }

    public void testResolver() throws Exception
    {
        Logger logger = new ConsoleLogger( ConsoleLogger.LEVEL_DEBUG );

        //
        // create the component to handle source resolution
        //

        final SourceResolverImpl resolver = new SourceResolverImpl();
        resolver.enableLogging( logger );

        //
        // create the context argument and set the root directory and 
        // contextualize the resolver
        //
        // NOTE: javadoc needed on contextualize method
        //

        final DefaultContext context = new DefaultContext();
        context.put( "context-root", new File( System.getProperty( "user.dir" ) ) );
        resolver.contextualize( context );

        //
        // create a service selector to be included in a service manager 
        // to be supplied to the resolver
        //

        final ResourceSourceFactory factory = new ResourceSourceFactory();
        factory.enableLogging( logger.getChildLogger( "factory" ) );

        // create a selector and add the factory to the selector,
        // add the selector to the manager, and service the resolver
        // NOTE: javadoc missing on the serviceable method
        //

        final DefaultServiceSelector selector = new DefaultServiceSelector();
        selector.put( "resource", factory );

        final DefaultServiceManager manager = new DefaultServiceManager();
        manager.put( SourceFactory.ROLE + "Selector", selector );

        logger.debug( "Servicing the resolver based on the fortress pattern - this will fail because the service implementation in SourceResolverImpl is attempting to narrow the selector to a ComponentSelector instead of a ServiceSelector - something needs to be changed here either in (a) the implementation or (b) this test case and in Fortrress." );

        resolver.service( manager );

        //
        // parameterize the resolver - Why?
        // NOTE: Missing javadoc
        //

        resolver.parameterize( new Parameters() );

        logger.debug( "resolver created - but is this correct ?" );

        //
        // setup a protocol handler - TO BE DONE
        //

        logger.debug( "help me - need to setup a handler" );

        //
        // test source URL creation - TO BE DONE
        //

        logger.debug( "help me - need to test source creation" );

        //
        // test source resolution - TO BE DONE
        //

        logger.debug( "help me - need to test source resolution" );

        assertTrue( true );
    }

}
