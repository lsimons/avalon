/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

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
package org.apache.avalon.fortress.impl.role;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.avalon.fortress.RoleManager;
import org.apache.avalon.fortress.impl.handler.FactoryComponentHandler;
import org.apache.avalon.fortress.impl.handler.PerThreadComponentHandler;
import org.apache.avalon.fortress.impl.handler.PoolableComponentHandler;
import org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler;
import org.apache.avalon.fortress.util.Service;
import org.apache.avalon.framework.activity.Initializable;

/**
 * ServiceRoleManager follows some simple rules to dynamically gather all
 * services and the meta-information into one role manager.  This really gets
 * rid of the need of multiple role managers.  It uses a set of entries in your
 * JARs to do its magic.
 *
 * <p><code><b>/services.list</b></code></p>
 *
 * <p>
 *   This lists all the services that are <em>defined</em> in this jar.
 * </p>
 *
 * <p><code><b>/META-INF/services/</b><i>my.class.Name</i></code></p>
 *
 * <p>
 *   One entry for each service where there are implementations for a role.  This
 *   follows the JAR services mechanism.
 * </p>
 *
 * <p><code><i>/my/class/Implementation.meta</i></code></p>
 *
 * <p>
 *   There is one entry sitting right beside every implementation class.  This
 *   holds all the meta information for the associated class.  It is a simple
 *   properties file.
 * </p>
 *
 * <h3>ANT Tasks available</h3>
 * <p>
 *   We have a couple of ANT tasks to make this really easy.  If you add this
 *   to your ANT build script (customizing it to make it work in your environment),
 *   it will make your life alot easier:
 * </p>
 *
 * <pre>
 *   &lt;taskdef name="collect-metainfo" classname="org.d_haven.guiapp.tools.ComponentMetaInfoCollector"&gt;
 *     &lt;classpath&gt;
 *       &lt;path refid="project.class.path"/&gt;
 *       &lt;pathelement path="${tools.dir}/guiapp-tools.jar"/&gt;
 *     &lt;/classpath&gt;
 *   &lt;/taskdef&gt;
 *
 *   &lt;taskdef name="collect-services" classname="org.d_haven.guiapp.tools.ServiceCollector"&gt;
 *     &lt;classpath&gt;
 *       &lt;path refid="project.class.path"/&gt;
 *       &lt;pathelement path="${tools.dir}/guiapp-tools.jar"/&gt;
 *     &lt;/classpath&gt;
 *   &lt;/taskdef&gt;
 *
 *   &lt;collect-metainfo destdir="${build.classes}"&gt;
 *      &lt;fileset dir="${src.dir}"/&gt;
 *   &lt;/collect-metainfo&gt;
 *
 *   &lt;collect-services inputjar="${build.dir}/${name}-core.jar"
 *                     outputjar="${lib.dir}/${name}.jar"/&gt;
 * </pre>
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class ServiceRoleManager extends AbstractRoleManager implements Initializable
{
    /** Translate from scope to component handler */
    private static final Map m_lifecycleMap;
    /** Used to split words in class names */
    private static final Pattern upperCase = Pattern.compile( "([A-Z]+)" );

    // Initialize the scope map
    static
    {
        Map lifecycleMap = new HashMap();
        lifecycleMap.put( "singleton", ThreadSafeComponentHandler.class.getName() );
        lifecycleMap.put( "thread", PerThreadComponentHandler.class.getName() );
        lifecycleMap.put( "pooled", PoolableComponentHandler.class.getName() );
        lifecycleMap.put( "transient", FactoryComponentHandler.class.getName() );

        m_lifecycleMap = Collections.unmodifiableMap( lifecycleMap );
    }

    /**
     * Create a ServiceRoleManager.
     */
    public ServiceRoleManager()
    {
        super( null );
    }

    /**
     * Create a ServiceRoleManager with a parent RoleManager.
     *
     * @param parent
     */
    public ServiceRoleManager( RoleManager parent )
    {
        super( parent, null );
    }

    /**
     * Create a ServiceRoleManager with the supplied classloader and
     * parent RoleManager.
     *
     * @param parent
     * @param loader
     */
    public ServiceRoleManager( RoleManager parent, ClassLoader loader )
    {
        super( parent, loader );
    }

    /**
     * Convert a Component implmentation classname into a shorthand
     * name.  It assumes all classnames for a particular component is
     * unique.
     *
     * @param string  The classname of a component
     * @return String the short name
     */
    public static final String createShortName( String className )
    {
        Matcher matcher = upperCase.matcher( className.substring( className.lastIndexOf( '.' ) + 1 ) );
        StringBuffer shortName = new StringBuffer();

        while( matcher.find() )
        {
            if( shortName.length() == 0 )
            {
                matcher.appendReplacement( shortName, "$1" );
            }
            else
            {
                matcher.appendReplacement( shortName, "-$1" );
            }
        }

        matcher.appendTail( shortName );

        return shortName.toString().toLowerCase();
    }

    /**
     * Initialize the ServiceRoleManager by looking at all the services and
     * classes available in the system.
     */
    public void initialize() throws Exception
    {
        Set services = new HashSet();

        Enumeration enum = getLoader().getResources( "services.list" );
        while( enum.hasMoreElements() )
        {
            readEntries( services, (URL)enum.nextElement() );
        }

        Iterator it = services.iterator();
        while( it.hasNext() )
        {
            String role = (String)it.next();
            getLogger().debug( "Adding service: " + role );
            try
            {
                setupImplementations( role );
            }
            catch( Exception e )
            {
                getLogger().debug( "Specified service '" + role + "' is not available", e );
            }
        }
    }

    /**
     * Get all the implementations of a service and set up their meta
     * information.
     *
     * @param role
     */
    private void setupImplementations( String role )
        throws IOException, ClassNotFoundException
    {
        Iterator it = Service.providers( getLoader().loadClass( role ) );

        while( it.hasNext() )
        {
            String impl = ( (Class)it.next() ).getName();
            getLogger().debug( "Reading meta info for " + impl );
            readMeta( role, impl );
        }
    }

    /**
     * Read the meta information in and actually add the role.
     *
     * @param role
     * @param implementation
     */
    private void readMeta( String role, String implementation )
    {
        Properties meta = new Properties();

        try
        {
            meta.load( getLoader().getResourceAsStream( translate( implementation ) ) );
        }
        catch( IOException ioe )
        {
            getLogger().error( "Could not load meta information for " +
                               implementation + ", skipping this class." );
            return;
        }

        String shortName = meta.getProperty( "x-avalon.name", createShortName( implementation ) );
        String handler = getHandler( meta );

        addRole( shortName, role, implementation, handler );
    }

    /**
     * Get the name of the requested component handler.
     *
     * @param meta
     * @return String
     */
    private String getHandler( Properties meta )
    {
        String lifecycle = meta.getProperty( "x-avalon.lifecycle", null );
        String handler = null;

        if( null != lifecycle )
        {
            handler = (String)m_lifecycleMap.get( lifecycle );
        }
        else
        {
            handler = meta.getProperty( "fortress.handler" );
        }

        if( null == handler )
        {
            handler = PerThreadComponentHandler.class.getName();
        }

        return handler;
    }

    /**
     * Translate a class name into the meta file name.
     *
     * @param implementation
     * @return String
     */
    private String translate( String implementation )
    {
        String entry = implementation.replace( '.', '/' );
        entry += ".meta";
        return entry;
    }

    /**
     * Read entries in a list file and add them all to the provided Set.
     *
     * @param services
     * @param url
     */
    private void readEntries( Set entries, URL url )
        throws IOException
    {
        BufferedReader reader = new BufferedReader( new InputStreamReader( url.openStream() ) );

        String entry = reader.readLine();
        while( entry != null )
        {
            entries.add( entry );
            entry = reader.readLine();
        }

        reader.close();
    }
}
