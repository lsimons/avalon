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

import org.apache.avalon.fortress.MetaInfoManager;
import org.apache.avalon.fortress.RoleManager;
import org.apache.avalon.fortress.util.Service;
import org.apache.avalon.framework.activity.Initializable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 * ServiceMetaManager follows some simple rules to dynamically gather all
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
 *   &lt;taskdef name="collect-metainfo" classname="org.apache.avalon.fortress.tools.ComponentMetaInfoCollector"&gt;
 *     &lt;classpath&gt;
 *       &lt;path refid="project.class.path"/&gt;
 *       &lt;pathelement path="${tools.dir}/guiapp-tools.jar"/&gt;
 *     &lt;/classpath&gt;
 *   &lt;/taskdef&gt;
 *
 *   &lt;collect-metainfo destdir="${build.classes}"&gt;
 *      &lt;fileset dir="${src.dir}"/&gt;
 *   &lt;/collect-metainfo&gt;
 * </pre>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.9 $
 */
public final class ServiceMetaManager extends AbstractMetaInfoManager implements Initializable
{
    /**
     * Create a ServiceMetaManager.
     */
    public ServiceMetaManager()
    {
        super( (MetaInfoManager) null );
    }

    /**
     * Create a ServiceMetaManager with a parent RoleManager.
     *
     * @param parent
     */
    public ServiceMetaManager( final RoleManager parent )
    {
        super( parent );
    }

    /**
     * Create a ServiceMetaManager with a parent RoleManager.
     *
     * @param parent
     */
    public ServiceMetaManager( final MetaInfoManager parent )
    {
        super( parent );
    }

    /**
     * Create a ServiceMetaManager with the supplied classloader and
     * parent RoleManager.
     *
     * @param parent
     * @param loader
     */
    public ServiceMetaManager( final MetaInfoManager parent, final ClassLoader loader )
    {
        super( parent, loader );
    }

    /**
     * Initialize the ServiceMetaManager by looking at all the services and
     * classes available in the system.
     *
     * @throws Exception if there is a problem
     */
    public void initialize() throws Exception
    {
        final Set services = new HashSet();

        final Enumeration enum = getLoader().getResources( "services.list" );
        while ( enum.hasMoreElements() )
        {
            readEntries( services, (URL) enum.nextElement() );
        }

        final Iterator it = services.iterator();
        while ( it.hasNext() )
        {
            final String role = (String) it.next();
            getLogger().debug( "Adding service: " + role );
            try
            {
                setupImplementations( role );
            }
            catch ( Exception e )
            {
                getLogger().debug( "Specified service '" + role + "' is not available", e );
            }
        }
    }

    /**
     * Get all the implementations of a service and set up their meta
     * information.
     *
     * @param role  The role name we are reading implementations for.
     *
     * @throws ClassNotFoundException if the role or component cannot be found
     */
    private void setupImplementations( final String role )
        throws ClassNotFoundException
    {
        final Iterator it = Service.providers( getLoader().loadClass( role ) );

        while ( it.hasNext() )
        {
            final String impl = ( (Class) it.next() ).getName();
            getLogger().debug( "Reading meta info for " + impl );
            if ( ! isAlreadyAdded( impl ) )
            {
                readMeta( role, impl );
            }
            else
            {
                // Mini-optimization: read meta info only once
                addComponent( role, impl, null, null );
            }
        }
    }

    /**
     * Read the meta information in and actually add the role.
     *
     * @param role
     * @param implementation
     */
    private void readMeta( final String role, final String implementation )
    {
        final Properties meta = new Properties();
        final List deps = new ArrayList();

        try
        {
            final InputStream stream =
                getLoader().getResourceAsStream( getMetaFile( implementation ) );

            if ( stream != null )
            {
                meta.load( stream );
            }
            else
            {
                getLogger().error(
                    "Meta information for " + implementation + 
                    " unavailable, skipping this class."
                );
                return;
            }
        }
        catch ( IOException ioe )
        {
            getLogger().error( "Could not load meta information for " +
                implementation + ", skipping this class." );
            return;
        }

        try
        {
            URL depURL = getLoader().getResource( getDepFile( implementation ) );

            if ( depURL == null )
            {
                if (getLogger().isDebugEnabled())
                {
                    getLogger().debug( "No dependencies for " + implementation + "." );
                }
            }
            else
            {
                HashSet set = new HashSet();
                readEntries( set, depURL );
                deps.addAll( set );
            }
        }
        catch ( Exception ioe )
        {
            getLogger().debug( "Could not load dependencies for " +
                implementation + ".", ioe );
        }

        addComponent( role, implementation, meta, deps );
    }

    /**
     * Translate a class name into the meta file name.
     *
     * @param implementation
     * @return String
     */
    private String getMetaFile( final String implementation )
    {
        String entry = implementation.replace( '.', '/' );
        entry += ".meta";
        return entry;
    }

    /**
     * Translate a class name into the meta file name.
     *
     * @param implementation
     * @return String
     */
    private String getDepFile( final String implementation )
    {
        String entry = implementation.replace( '.', '/' );
        entry += ".deps";
        return entry;
    }

    /**
     * Read entries in a list file and add them all to the provided Set.
     *
     * @param entries
     * @param url
     *
     * @throws IOException if we cannot read the entries
     */
    private void readEntries( final Set entries, final URL url )
        throws IOException
    {
        final BufferedReader reader = new BufferedReader( new InputStreamReader( url.openStream() ) );

        try
        {
            String entry = reader.readLine();
            while ( entry != null )
            {
                entries.add( entry );
                entry = reader.readLine();
            }
        }
        finally
        {
            reader.close();
        }
    }
}