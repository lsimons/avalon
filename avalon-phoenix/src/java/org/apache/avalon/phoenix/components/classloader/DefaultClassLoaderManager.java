/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.classloader;

import java.io.File;
import java.net.URL;
import java.security.Policy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.excalibur.extension.Extension;
import org.apache.avalon.excalibur.packagemanager.ExtensionManager;
import org.apache.avalon.excalibur.packagemanager.PackageManager;
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
import org.apache.avalon.phoenix.interfaces.ClassLoaderManager;
import org.apache.excalibur.loader.builder.LoaderBuilder;
import org.apache.excalibur.loader.builder.LoaderResolver;
import org.apache.excalibur.loader.metadata.ClassLoaderMetaData;
import org.apache.excalibur.loader.metadata.ClassLoaderSetMetaData;
import org.apache.excalibur.loader.metadata.FileSetMetaData;
import org.apache.excalibur.loader.metadata.JoinMetaData;
import org.apache.excalibur.loader.verifier.ClassLoaderVerifier;
import org.apache.excalibur.policy.builder.PolicyBuilder;
import org.apache.excalibur.policy.metadata.PolicyMetaData;
import org.apache.excalibur.policy.reader.PolicyReader;
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
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @see ClassLoaderManager
 */
public class DefaultClassLoaderManager
    extends AbstractLogEnabled
    implements ClassLoaderManager, Contextualizable, Serviceable, Initializable
{
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
     * The map of predefined ClassLoaders. In the current incarnation this only
     * contains the system classloader.
     */
    private Map m_predefinedLoaders;

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
    public void contextualize( Context context )
        throws ContextException
    {
        m_commonClassLoader = (ClassLoader)context.get( "common.classloader" );
    }

    /**
     * @avalon.dependency interface="ExtensionManager"
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
     * @param source the source of application. (usually the name of the .sar file
     *               or else the same as baseDirectory)
     * @param homeDirectory the base directory of application
     * @param workDirectory the work directory of application
     * @return the ClassLoader created
     * @throws Exception if an error occurs
     */
    public ClassLoader createClassLoader( final Configuration environment,
                                          final File source,
                                          final File homeDirectory,
                                          final File workDirectory )
        throws Exception
    {
        //Configure policy
        final Configuration policyConfig = environment.getChild( "policy" );
        final Policy policy =
            configurePolicy( policyConfig, homeDirectory, workDirectory );

        final ClassLoaderSetMetaData metaData =
            getLoaderMetaData( environment );

        m_verifier.verifyClassLoaderSet( metaData );

        final LoaderResolver resolver =
            new SarLoaderResolver( m_packageManager, policy,
                                   homeDirectory, workDirectory );
        setupLogger( resolver );
        final Map map =
            m_builder.buildClassLoaders( metaData, resolver, m_predefinedLoaders );
        return (ClassLoader)map.get( metaData.getDefault() );
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
    {
        final boolean loaderDefined = isClassLoaderDefined( environment );
        if( !loaderDefined )
        {
            return createDefaultLoaderMetaData();
        }
        else
        {
            throw new IllegalStateException( "Not implemented yet");
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
        return null != environment.getChild( "classloader", false );
    }

    /**
     * Setup policy based on configuration data.
     *
     * @param configuration the configuration data
     * @param baseDirectory the applications base directory
     * @throws ConfigurationException if an error occurs
     */
    private Policy configurePolicy( final Configuration configuration,
                                    final File baseDirectory,
                                    final File workDirectory )
        throws ConfigurationException
    {
        final SarPolicyResolver resolver =
            new SarPolicyResolver( baseDirectory, workDirectory );
        setupLogger( resolver );
        final PolicyBuilder builder = new PolicyBuilder();
        final PolicyReader reader = new PolicyReader();
        final SarPolicyVerifier verifier = new SarPolicyVerifier();
        setupLogger( verifier );

        final Element element = ConfigurationUtil.toElement( configuration );
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
