package org.apache.avalon.apps.sevak.demo;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.apps.sevak.MultihostSevak;
import org.apache.avalon.phoenix.BlockContext;

import java.io.File;

/**
 * @phoenix:block
 *
 * Test Sevak (Tomcat wrapper).
 *
 * @author  Daniel Krieg<dkrieg@kc.rr.com>
 * @version 1.0
 */
public class MultihostSevakTest extends AbstractLogEnabled
        implements Contextualizable, Serviceable, Configurable, Initializable {

    private BlockContext m_context;
    private Configuration m_configuration;
    private MultihostSevak m_mulihostSevak;

    public void contextualize( Context context ) throws ContextException {
        getLogger().info( "MultihostSevakTest.contextualize" );
        m_context = (BlockContext) context;
    }

    public void configure( Configuration configuration ) throws ConfigurationException {
        getLogger().info( "MultihostSevakTest.configure" );
        m_configuration = configuration;
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable
     * @phoenix:dependency name="org.apache.avalon.apps.sevak.MultihostSevak"
     */
    public void service( ServiceManager serviceManager ) throws ServiceException {
        getLogger().info( "MultihostSevakTest.service" );
        m_mulihostSevak = (MultihostSevak) serviceManager.lookup( MultihostSevak.ROLE );
    }

    public void initialize() throws Exception {
        getLogger().info( "MultihostSevakTest.initialize" );
        Configuration[] contexts = m_configuration.getChildren( "Context" );
        for( int i = 0; i < contexts.length; i++ ) {
            String ctx = contexts[ i ].getAttribute( "docBase" );
            String ctxPath = contexts[ i ].getAttribute( "path" );
            ctxPath = ctxPath.replace( '/', File.separatorChar );
            ctxPath = ctxPath.replace( '\\', File.separatorChar );
            String ctxFullPath = m_context.getBaseDirectory().getAbsolutePath() + File.separatorChar + "SAR-INF"
                     + File.separatorChar + "lib" + File.separatorChar + ctxPath;
            //System.out.println("Ctx = " + ctx + ", path = " + ctxFullPath);
            m_mulihostSevak.deploy( "localhost", ctx, new File( ctxFullPath ) );
        }
    }
}
