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

package org.apache.avalon.composition.logging.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.avalon.composition.logging.LoggingManager;
import org.apache.avalon.composition.logging.LoggerException;
import org.apache.avalon.composition.logging.LoggingDescriptor;
import org.apache.avalon.composition.logging.TargetDescriptor;
import org.apache.avalon.composition.logging.TargetProvider;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.composition.data.CategoryDirective;
import org.apache.avalon.composition.data.CategoriesDirective;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.output.io.FileTarget;
import org.apache.log.output.io.StreamTarget;

/**
 * A <code>LoggerManager</code> interface declares operation supporting
 * the management of a logging hierarchy.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public class DefaultLoggingManager implements LoggingManager
{
    //---------------------------------------------------------------
    // static
    //---------------------------------------------------------------

    private static final Resources REZ =
            ResourceManager.getPackageResources( DefaultLoggingManager.class );

    private static final StreamTarget DEFAULT_STREAM =
            new StreamTarget( System.out, new StandardFormatter( DEFAULT_FORMAT ) );

    //---------------------------------------------------------------
    // state
    //---------------------------------------------------------------

    /**
     * Base directory for file targets.
     */
    private File m_baseDirectory;

    /**
     * The list of named logging targets.
     */
    private final HashMap m_targets = new HashMap();

    /**
     * The implementation log hierarchy.
     */
    private Hierarchy m_hierarchy = new Hierarchy();

    /**
     * The bootstrap logging channel.
     */
    private org.apache.avalon.framework.logger.Logger m_logger;

    private final boolean m_debug;

    //==============================================================
    // constructor
    //==============================================================

    /**
     * <p>Application of a runtime context to the manager.
     * The context value will be passed directly to lifestyle handlers
     * established by this service.
     * @param base the directory for logging targets
     * @param descriptor the logging system descriptor (may be null)
     */
    public DefaultLoggingManager( 
       File base, LoggingDescriptor descriptor ) throws Exception
    {
        this( base, descriptor, false );
    }

    /**
     * <p>Application of a runtime context to the manager.
     * The context value will be passed directly to lifestyle handlers
     * established by this service.
     *
     * @param base the directory for logging targets
     * @param descriptor the logging system descriptor (may be null)
     * @param debug a debug flag
     */
    public DefaultLoggingManager( 
       File base, LoggingDescriptor descriptor, boolean debug ) throws Exception
    {
        if( descriptor == null ) throw new NullPointerException( "descriptor" );
        if( base == null ) throw new NullPointerException( "base" );

        m_baseDirectory = base;
        m_debug = debug;

        //
        // setup the hierarchy, default logging target, and default priority
        //

        String defaultPriority = DEFAULT_PRIORITY ;
        getHierarchy().setDefaultLogTarget( DEFAULT_STREAM );
        m_targets.put( DEFAULT_TARGET, DEFAULT_STREAM );
        if( !debug )
        {
            if( descriptor.getPriority() != null )
            {
                defaultPriority = descriptor.getPriority();
                getHierarchy().setDefaultPriority( 
                  Priority.getPriorityForName( defaultPriority ) ); 
            }
            else
            {
                getHierarchy().setDefaultPriority( 
                  Priority.getPriorityForName( defaultPriority ) ); 
            }
        }
        else
        {
            defaultPriority = "DEBUG";
            getHierarchy().setDefaultPriority( 
                  Priority.getPriorityForName( "DEBUG" ) ); 
        }

        //
        // build targets based on the information contained in the descriptor
        //

        TargetDescriptor[] targets = descriptor.getTargetDescriptors();
        for( int i = 0; i < targets.length; i++ )
        {
            TargetDescriptor target = targets[i];
            addTarget( targets[i] );
        }

        //
        // set the default target
        //

        String name = descriptor.getTarget();
        if( name != null )
        {
            LogTarget target = (LogTarget) m_targets.get( name );
            if( target != null )
            {
                getHierarchy().setDefaultLogTarget( target );
            }
            else
            {
                throw new LoggerException(
                  "Supplied default logging target: '"
                  + name + "' does not exist." );
            }
        }
        else
        {
            getHierarchy().setDefaultLogTarget( DEFAULT_STREAM );
        }

        addCategories( descriptor.getName(), descriptor );

        final String channel = descriptor.getName() + ".logging";
        m_logger = getLoggerForCategory( channel );
        if( m_debug )
        {
            log( "default priority: " + defaultPriority );
        }
    }

    //===============================================================
    // LoggingManager
    //===============================================================

    /**
     * Add a set of category entries using the supplied categories descriptor.
     *
     * @param descriptor a set of category descriptors to be added under the path
     */
    public void addCategories( CategoriesDirective descriptor )
    {
        addCategories( "", descriptor );
    }

    /**
     * Add a set of category entries relative to the supplied base category
     * path, using the supplied descriptor as the definition of subcategories.
     *
     * @param root the category base path
     * @param directive a category directive to add
     */
    public void addCategories( String root, CategoriesDirective directive )
    {
        final String path = filter( root );
        addCategory( path, directive.getPriority(), directive.getTarget() );
        CategoryDirective[] categories = directive.getCategories();
        for( int i = 0; i < categories.length; i++ )
        {
            CategoryDirective category = categories[i];
            final String priority = category.getPriority();
            final String target = category.getTarget();
            final String name = filter( category.getName() );
            if( path.equals( "" ) )
            {
                addCategory( name, priority, target );
            }
            else
            {
                final String base = filter( path + "." + name );
                addCategory( base, priority, target );
            }
        }
    }

    /**
     * Create a logging channel configured with the supplied category path,
     * priority and target.
     *
     * @param name logging category path
     * @param target the logging target to assign the channel to
     * @param priority the priority level to assign to the channel
     * @return the logging channel
     * @throws Exception if an error occurs
     */
    public org.apache.avalon.framework.logger.Logger getLoggerForCategory(
            final String name, String target, String priority )
            throws Exception
    {
        return new LogKitLogger( addCategory( name, target, priority ) );
    }

    /**
     * Configure Logging channel based on the description supplied in a
     * category descriptor.
     *
     * @param category defintion of the channel category, priority and target
     * @return the logging channel
     * @throws Exception if an error occurs
     */
    public org.apache.avalon.framework.logger.Logger getLoggerForCategory( 
      final CategoryDirective category )
      throws Exception
    {
        return new LogKitLogger(
                addCategory(
                        category.getName(),
                        category.getPriority(),
                        category.getTarget()
                )
        );
    }

    /**
     * Return the Logger for the specified category.
     * @param category the category path
     * @return the logging channel
     */
    public org.apache.avalon.framework.logger.Logger getLoggerForCategory( final String category )
    {
        Logger log = addCategory( category, null, null );
        return new LogKitLogger( log );
    }

    //===============================================================
    // implementation
    //===============================================================

    private Logger addCategory( String path, String priority, String target )
    {
        return addCategory( path, priority, target, true );
    }

    private Logger addCategory( String path, String priority, String target, boolean notify )
    {
        final String name = filter( path );
        final Logger logger;

        if( m_debug ) log( "adding category: " + name + ", " + priority );

        try
        {
            logger = getHierarchy().getLoggerFor( name );
        }
        catch( Throwable e )
        {
            throw new RuntimeException( "Bad category: " + name );
        }

        if( !m_debug && priority != null )
        {
            //System.out.println( "## setting [" + name + "] to: " + priority );
            final Priority priorityValue = Priority.getPriorityForName( priority );
            if( !priorityValue.getName().equals( priority ) )
            {
                final String message = REZ.getString( "unknown-priority", priority, name );
                throw new IllegalArgumentException( message );
            }
            logger.setPriority( priorityValue );
        }

        if( target != null )
        {
            if( !target.equals( "default" ) )
            {
                final LogTarget logTarget = (LogTarget) m_targets.get( target );
                if( logTarget != null )
                {
                    logger.setLogTargets( new LogTarget[]{ logTarget } );
                }
            }
        }

        return logger;
    }

    private String filter( String name )
    {
        if( name == null ) return "";
        String path = name.replace( '/', '.' );
        if( path.startsWith( "." ) )
        {
            path = path.substring( 1 );
            return filter( path );
        }
        if( path.endsWith( "." ) )
        {
            path = path.substring( 0, path.length() -1 );
            return filter( path );
        }
        return path;
    }

    private void addTarget( TargetDescriptor target ) throws Exception
    {
        final String name = target.getName();
        TargetProvider provider = target.getProvider();
        if( provider instanceof FileTargetProvider )
        {
            FileTargetProvider fileProvider = (FileTargetProvider) provider;
            String filename = fileProvider.getLocation();
            final StandardFormatter formatter = new StandardFormatter( DEFAULT_FORMAT );
            File file = new File( m_baseDirectory, filename );
            try
            {
                FileTarget logTarget = 
                  new FileTarget( file.getAbsoluteFile(), false, formatter );
                m_targets.put( name, logTarget );
            } catch( final IOException ioe )
            {
                final String message =
                        REZ.getString( "target.nocreate", name, file, ioe.getMessage() );
                throw new LoggerException( message, ioe );
            }
        } else
        {
            final String error =
                    "Unrecognized logging provider: " + provider.getClass().getName();
            throw new IllegalArgumentException( error );
        }
    }

    /**
     * Return the internal Logger.
     */
    private org.apache.avalon.framework.logger.Logger getLogger()
    {
        return m_logger;
    }

    private Hierarchy getHierarchy()
    {
        return m_hierarchy;
    }

    private void log( String message )
    {
        if( getLogger() != null )
        {
            getLogger().debug( message );
        }
    }
}
