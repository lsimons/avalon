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

package org.apache.avalon.phoenix.components.classloader;

import java.io.File;
import java.net.URL;
import java.security.Policy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.excalibur.extension.Extension;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.components.extensions.pkgmgr.ExtensionManager;
import org.apache.avalon.phoenix.components.extensions.pkgmgr.PackageManager;
import org.apache.avalon.phoenix.interfaces.ClassLoaderManager;
import org.apache.avalon.phoenix.interfaces.ClassLoaderSet;
import org.realityforge.classman.builder.LoaderBuilder;
import org.realityforge.classman.builder.LoaderResolver;
import org.realityforge.classman.metadata.ClassLoaderMetaData;
import org.realityforge.classman.metadata.ClassLoaderSetMetaData;
import org.realityforge.classman.metadata.FileSetMetaData;
import org.realityforge.classman.metadata.JoinMetaData;
import org.realityforge.classman.reader.ClassLoaderSetReader;
import org.realityforge.classman.verifier.ClassLoaderVerifier;
import org.realityforge.configkit.PropertyExpander;
import org.realityforge.xmlpolicy.builder.PolicyBuilder;
import org.realityforge.xmlpolicy.metadata.PolicyMetaData;
import org.realityforge.xmlpolicy.reader.PolicyReader;
import org.w3c.dom.Element;

/**
 * Component that creates and manages the {@link ClassLoader}
 * for an application loaded out of a <tt>.sar</tt> deployment.
 *
 * <p>Currently it creates a policy based on the policy declaration
 * in the configuration. It then just creates a URLClassLoader and
 * populates it with the specified codebase {@link URL}s.</p>
 *
 * <p>In the future this class will scan the manifests for "Optional
 * Packages" formely called "Extensions" which it will add to the
 * {@link ClassLoader}</p>
 *
 * @author Peter Donald
 * @see ClassLoaderManager
 */
public class DefaultClassLoaderManager
    extends AbstractLogEnabled
    implements ClassLoaderManager, Contextualizable, Serviceable, Initializable
{
    /**
     * Constant for name of element that indicates custom
     * classloader tree to define.
     */
    private static final String CLASSLOADERS_ELEMENT = "classloaders";

    /**
     * Component to manage "Optional Packages" aka
     * Extensions to allow programs to declare dependencies
     * on such extensions.
     */
    private PackageManager m_packageManager;

    /**
     * Parent ClassLoader for all applications
     * aka as the "common" classloader.
     */
    private ClassLoader m_commonClassLoader;

    /**
     * The utility class used to verify {@link ClassLoaderMetaData} objects.
     */
    private final ClassLoaderVerifier m_verifier = new ClassLoaderVerifier();

    /**
     * Utility class to build map of {@link ClassLoader} objects.
     */
    private final LoaderBuilder m_builder = new LoaderBuilder();

    /**
     * Utility class to read {@link ClassLoaderSetMetaData} objects
     * from XML trees.
     */
    private final ClassLoaderSetReader m_reader = new ClassLoaderSetReader();

    /**
     * The map of predefined ClassLoaders. In the current incarnation this only
     * contains the system classloader.
     */
    private Map m_predefinedLoaders;

    /**
     * The contextdata used in interpolation of the policy configuration file.
     */
    private final Map m_data = new HashMap();

    /**
     * The property expander that will expand properties in the policy configuraiton file.
     */
    private final PropertyExpander m_expander = new PropertyExpander();

    /**
     * Pass the Context to the Manager.
     * It is expected that the there will be an entry
     * <ul>
     *   <b>common.classloader</b> : ClassLoader shared betweeen
     *      container and applications</li>
     * </ul>
     *
     * @param context the context
     * @throws ContextException if context does not contain common classloader
     */
    public void contextualize( final Context context )
        throws ContextException
    {
        m_commonClassLoader = (ClassLoader)context.get( "common.classloader" );
    }

    /**
     * @phoenix.dependency interface="ExtensionManager"
     */
    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        final ExtensionManager extensionManager =
            (ExtensionManager)serviceManager.lookup( ExtensionManager.ROLE );
        m_packageManager = new PackageManager( extensionManager );
    }

    /**
     * Setup the map of predefined classloaders.
     *
     * @throws Exception if unable to setup map
     */
    public void initialize()
        throws Exception
    {
        m_data.putAll( System.getProperties() );

        final Map defined = new HashMap();
        defined.put( "*system*", m_commonClassLoader );
        m_predefinedLoaders = Collections.unmodifiableMap( defined );
    }

    /**
     * Create a {@link ClassLoader} for a specific application.
     * See Class Javadoc for description of technique for creating
     * {@link ClassLoader}.
     *
     * @param environment the configuration "environment.xml" for the application
     * @param homeDirectory the base directory of application
     * @param workDirectory the work directory of application
     * @return the ClassLoader created
     * @throws Exception if an error occurs
     */
    public ClassLoaderSet createClassLoaderSet( final Configuration environment,
                                                final Map data,
                                                final File homeDirectory,
                                                final File workDirectory )
        throws Exception
    {
        //Configure policy
        final Configuration policyConfig = environment.getChild( "policy" );
        final Policy policy =
            configurePolicy( policyConfig, data, homeDirectory, workDirectory );

        final ClassLoaderSetMetaData metaData =
            getLoaderMetaData( environment );

        m_verifier.verifyClassLoaderSet( metaData );

        final LoaderResolver resolver =
            new SarLoaderResolver( m_packageManager, policy,
                                   homeDirectory, workDirectory );
        setupLogger( resolver );
        final Map map =
            m_builder.buildClassLoaders( metaData, resolver, m_predefinedLoaders );
        final ClassLoader defaultClassLoader =
            (ClassLoader)map.get( metaData.getDefault() );
        return new ClassLoaderSet( defaultClassLoader, map );
    }

    /**
     * Extract the {@link ClassLoaderMetaData} from the environment
     * configuration. If no &lt;classloader/&gt; section is defined
     * in the config file then a default metadata will be created.
     *
     * @param environment the environment configuration
     * @return the {@link ClassLoaderMetaData} object
     */
    private ClassLoaderSetMetaData getLoaderMetaData( final Configuration environment )
        throws Exception
    {
        final boolean loaderDefined = isClassLoaderDefined( environment );
        if( !loaderDefined )
        {
            return createDefaultLoaderMetaData();
        }
        else
        {
            final Configuration loaderConfig = environment.getChild( CLASSLOADERS_ELEMENT );
            final Element element = ConfigurationUtil.toElement( loaderConfig );
            return m_reader.build( element );
        }
    }

    /**
     * Create the default {@link ClassLoaderSetMetaData}. The
     * default metadata includes all jars in the /SAR-INF/lib/ directory
     * in addition to the /SAR-INF/classes/ directory.
     *
     * @return the default {@link ClassLoaderSetMetaData} object
     */
    private ClassLoaderSetMetaData createDefaultLoaderMetaData()
    {
        final String[] includes = new String[]{"SAR-INF/lib/*.jar"};
        final String[] excludes = new String[ 0 ];
        final FileSetMetaData fileSet =
            new FileSetMetaData( ".",
                                 includes,
                                 excludes );
        final String name = "default";
        final String parent = "*system*";
        final String[] entrys = new String[]{"SAR-INF/classes/"};
        final Extension[] extensions = new Extension[ 0 ];
        final FileSetMetaData[] filesets = new FileSetMetaData[]{fileSet};
        final ClassLoaderMetaData loader =
            new ClassLoaderMetaData( name, parent, entrys, extensions, filesets );
        final String[] predefined = new String[]{parent};
        final ClassLoaderMetaData[] classLoaders = new ClassLoaderMetaData[]{loader};
        final JoinMetaData[] joins = new JoinMetaData[ 0 ];
        return
            new ClassLoaderSetMetaData( name,
                                        predefined,
                                        classLoaders,
                                        joins );
    }

    /**
     * Return true if environment config defines "classloader" element, false otherwise.
     *
     * @param environment the environment config
     * @return true if environment config defines "classloader" element, false otherwise.
     */
    private boolean isClassLoaderDefined( final Configuration environment )
    {
        return null != environment.getChild( CLASSLOADERS_ELEMENT, false );
    }

    /**
     * Setup policy based on configuration data.
     *
     * @param configuration the configuration data
     * @param data the context data used to expand policy file
     * @param baseDirectory the applications base directory
     * @throws ConfigurationException if an error occurs
     */
    private Policy configurePolicy( final Configuration configuration,
                                    final Map data,
                                    final File baseDirectory,
                                    final File workDirectory )
        throws Exception
    {
        final SarPolicyResolver resolver =
            new SarPolicyResolver( baseDirectory, workDirectory );
        setupLogger( resolver );
        final PolicyBuilder builder = new PolicyBuilder();
        final PolicyReader reader = new PolicyReader();
        final SarPolicyVerifier verifier = new SarPolicyVerifier();
        setupLogger( verifier );

        final Element element = ConfigurationUtil.toElement( configuration );
        final HashMap newData = new HashMap();
        newData.putAll( m_data );
        newData.putAll( data );
        newData.put( "/", File.separator );

        m_expander.expandValues( element, newData );

        element.setAttribute( "version", "1.0" );
        try
        {
            final PolicyMetaData policy = reader.readPolicy( element );
            verifier.verifyPolicy( policy );
            return builder.buildPolicy( policy, resolver );
        }
        catch( final Exception e )
        {
            throw new ConfigurationException( e.getMessage(), e );
        }
    }
}
