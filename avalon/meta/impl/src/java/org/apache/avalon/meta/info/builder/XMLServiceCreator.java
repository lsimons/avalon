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

package org.apache.avalon.meta.info.builder;

import java.io.InputStream;
import java.util.Properties;
import java.util.ArrayList;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.Version;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.meta.ConfigurationBuilder;
import org.apache.avalon.meta.info.Service;
import org.apache.avalon.meta.info.EntryDescriptor;
import org.apache.avalon.meta.info.ReferenceDescriptor;
import org.apache.avalon.meta.info.builder.BuildException;
import org.apache.excalibur.configuration.ConfigurationUtil;
import org.xml.sax.InputSource;

/**
 * Utility class the handles the internalization of an XML description
 * of a service into a {@link Service} instance.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 08:15:36 $
 */
public class XMLServiceCreator
    implements ServiceCreator
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( XMLServiceCreator.class );

    /**
     * Create a {@link Service} from stream
     *
     * @param key the name of the service type
     * @param input the input stream that the resource is loaded from
     * @return the created {@link Service}
     * @exception Exception if an error occurs
     */
    public Service createService( String key, InputStream input )
        throws Exception
    {
        if( input == null )
        {
            throw new NullPointerException( "input" );
        }

        final InputSource source = new InputSource( input );
        final Configuration xservice = ConfigurationBuilder.build( source );
        return build( key, xservice );
    }

    /**
     * Create a {@link Service} object for a supplied classname from
     * configuration data.
     *
     * @param classname the classname of the service
     * @param info the service defintion
     * @return the created Service
     * @throws BuildException if an error occurs
     */
    private Service build( final String classname, final Configuration info )
        throws BuildException
    {
        final String topLevelName = info.getName();
        if( !topLevelName.equals( "service" ) )
        {
            final String message =
                REZ.getString( "builder.bad-toplevel-service-element.error",
                               classname,
                               topLevelName );
            throw new BuildException( message );
        }

        final Properties attributes =
            buildAttributes( info.getChild( "attributes" ) );
        final EntryDescriptor[] entries =
            buildEntries( info.getChild( "entries" ).getChildren("entry") );
        final String versionString = info.getChild( "version" ).getValue( "1.0" );
        final Version version = buildVersion( versionString );

        return new Service( new ReferenceDescriptor( classname, version ), entries, attributes );
    }

    /**
     * Build up a list of attributes from specific config tree.
     *
     * @param config the attributes config
     * @return the Properties object representing attributes
     * @throws ConfigurationException if an error occurs
     */
    public Properties buildAttributes( final Configuration config )
        throws BuildException
    {
        final Properties attributes = new Properties();
        final Configuration[] children = config.getChildren( "attribute" );
        for( int i = 0; i < children.length; i++ )
        {
            Configuration child = children[ i ];
            String key = null;
            try
            {
                key = child.getAttribute( "key" );
            }
            catch( ConfigurationException ce )
            {
                final String error =
                  "Missing 'key' attribute in 'attribute' element.\n"
                  + ConfigurationUtil.list( child );
                throw new BuildException( error, ce );
            }

            String value = null;
            try
            {
                value = child.getAttribute( "value" );
            }
            catch( Throwable e )
            {
                value = child.getValue( "" );
            }
            attributes.setProperty( key, value );
        }
        return attributes;
    }

    /**
     * A utility method to build an array of {@link EntryDescriptor}
     * objects from specified configuration.
     *
     * @param entrySet the set of entrys to build
     * @return the created {@link EntryDescriptor}s
     * @throws ConfigurationException if an error occurs
     */
    protected EntryDescriptor[] buildEntries( final Configuration[] entrySet )
        throws BuildException
    {
        final ArrayList entrys = new ArrayList();

        for( int i = 0; i < entrySet.length; i++ )
        {
            final EntryDescriptor service = buildEntry( entrySet[ i ] );
            entrys.add( service );
        }

        return (EntryDescriptor[])entrys.toArray( new EntryDescriptor[ entrys.size() ] );
    }

    /**
     * Create a {@link EntryDescriptor} from configuration.
     *
     * @param config the configuration
     * @return the created {@link EntryDescriptor}
     * @throws ConfigurationException if an error occurs
     */
    protected EntryDescriptor buildEntry( final Configuration config )
        throws BuildException
    {
        try
        {
            final String key = config.getAttribute( "key" );
            final String type = config.getAttribute( "type", "java.lang.String" );
            final boolean isVolatile = config.getAttributeAsBoolean( "volatile", false );
            final boolean optional =
              config.getAttributeAsBoolean( "optional", false );
            final String alias = config.getAttribute( "alias", null );
            return new EntryDescriptor( key, type, optional, isVolatile, alias );
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to build entry descriptor."
              + ConfigurationUtil.list( config );
            throw new BuildException( error, e );
        }
    }

    /**
     * A utility method to parse a Version object from specified string.
     *
     * @param version the version string
     * @return the created Version object
     */
    protected Version buildVersion( final String version ) 
      throws BuildException
    {
        try
        {
            return Version.getVersion( version );
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( "builder.bad-version", version );
            throw new BuildException( error, e );
        }
    }
}
