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
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.meta.info.Type;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.meta.ConfigurationBuilder;
import org.xml.sax.InputSource;

/**
 * A TypeBuilder is responsible for building {@link Type}
 * objects from Configuration objects. The format for Configuration object
 * is specified in the <a href="package-summary.html#external">package summary</a>.
 *
 * <p><b>UML</b></p>
 * <p><image src="doc-files/TypeBuilder.gif" border="0"/></p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2003/11/25 23:58:55 $
 */
public final class TypeBuilder
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( TypeBuilder.class );

    private final TypeFactory m_xmlTypeFactory = createxmlTypeFactory();
    private final TypeCreator m_serialTypeCreator = new SerializedTypeCreator();

    /**
     * Create a {@link Type} object for specified Class.
     *
     * @param clazz The class of Component
     * @return the created Type
     * @throws Exception if an error occurs
     */
    public Type buildType( final Class clazz )
        throws Exception
    {
        //
        // build the configuration defaults and the profiles and
        // supply these as arguments to thye type build
        //

        final Type info = buildFromSerDescriptor( clazz );
        if( null != info )
        {
            return info;
        }
        else
        {
            return buildFromXMLDescriptor( clazz );
        }
    }

    /**
     * Build Type from the XML descriptor format.
     *
     * @param clazz The class for Component
     * @return the created Type
     * @throws Exception if an error occurs
     */
    private Type buildFromSerDescriptor( final Class clazz )
        throws Exception
    {
        Type type = buildFromSerDescriptor( clazz, ".ztype" );
        if( type != null )
        {
            return type;
        }
        else
        {
            return buildFromSerDescriptor( clazz, ".zinfo" );
        }
    }

    private Type buildFromSerDescriptor( final Class clazz, String form )
        throws Exception
    {
        final String classname = clazz.getName();
        final ClassLoader classLoader = clazz.getClassLoader();
        final String address = classname.replace( '.', '/' ) + form;
        final InputStream stream =
            classLoader.getResourceAsStream( address );
        if( null == stream )
        {
            return null;
        }
        return m_serialTypeCreator.createType( classname, stream );
    }

    /**
     * Build Type from the XML descriptor format.  The implementation
     * will attempt to locate a &lt;classname&gt;.xtype resource.  If
     * not found, the implementation will attempt to locate
     * a &lt;classname&gt;.info resource.  Once a resource is established
     * the type will be resolved relative to the root element.  Normally
     * the root element is a &lttype&gt;, however the implementation
     * also recognises the legacy &lt;blockinfo&gt; schema.
     *
     * @param clazz The class for Component
     * @return the created Type
     * @throws Exception if an error occurs
     */
    private Type buildFromXMLDescriptor( Class clazz )
        throws Exception
    {
        final String classname = clazz.getName();
        final ClassLoader classLoader = clazz.getClassLoader();

        final TypeFactory xmlTypeFactory = getXMLTypeFactory();

        //
        // get the input stream for the .xtype resource
        //

        String path =
            classname.replace( '.', '/' ) + ".xtype";
        InputStream inputStream =
            classLoader.getResourceAsStream( path );

        if( null == inputStream )
        {
            path =
              classname.replace( '.', '/' ) + ".xinfo";
            inputStream =
              classLoader.getResourceAsStream( path );
        }

        if( null == inputStream )
        {
            final String message =
                REZ.getString( "builder.missing-info.error", path );
            throw new Exception( message );
        }

        final InputSource inputSource = new InputSource( inputStream );
        final Configuration xinfo = ConfigurationBuilder.build( inputSource );

        final String xdefaults =
            classname.replace( '.', '/' ) + ".xconfig";
        final InputStream defaultsStream =
            classLoader.getResourceAsStream( xdefaults );

        Configuration defaults;
        if( defaultsStream != null )
        {
            final InputSource defaultsSource = new InputSource( defaultsStream );
            defaults = resolveConfiguration(
              classLoader, ConfigurationBuilder.build( defaultsSource ) );
        }
        else
        {
            defaults = new DefaultConfiguration( "configuration", (String) null );
        }

        //
        // build the type
        //

        return xmlTypeFactory.createType( classname, xinfo, defaults );
    }

    private Configuration resolveConfiguration( ClassLoader classloader, Configuration config )
      throws Exception
    {
        if( config == null )
        {
            throw new NullPointerException("config");
        }
        String src = config.getAttribute( "src", null );
        if( src == null )
        {
            return config;
        }
        else
        {
            if( src.startsWith( "resource://" ) )
            {
                final String url = src.substring( 11 );
                final InputStream stream =
                    classloader.getResourceAsStream( url );
                if( null == stream )
                {
                    final String error =
                        "Requested configuration source does not exist: " + src;
                    throw new ConfigurationException( error );
                }
                final InputSource source = new InputSource( stream );
                return resolveConfiguration(
                  classloader, ConfigurationBuilder.build( source ) );
            }
            else
            {
                try
                {
                    return resolveConfiguration(
                      classloader, ConfigurationBuilder.build( src ) );
                }
                catch( Throwable e )
                {
                    final String error =
                        "Unexpected exception while attempting to resolve configuration from src : "
                        + src;
                    throw new ConfigurationException( error, e );
                }
            }
        }
    }

    /**
     * Utility to get xml info builder, else throw
     * an exception if missing descriptor.
     *
     * @return the TypeCreator
     */
    private TypeFactory getXMLTypeFactory()
        throws Exception
    {
        if( null != m_xmlTypeFactory )
        {
            return m_xmlTypeFactory;
        }
        else
        {
            final String message =
                REZ.getString( "builder.missing-xml-creator.error" );
            throw new Exception( message );
        }
    }

    /**
     * Utility to get xmlTypeFactory if XML files are on
     * ClassPath.
     *
     * @return the XML {@link TypeFactory}
     */
    private static TypeFactory createxmlTypeFactory()
    {
        TypeFactory xmlTypeFactory = null;
        try
        {
            xmlTypeFactory = new XMLTypeCreator();
        }
        catch( final Exception e )
        {
            //Ignore it if ClassNot found due to no
            //XML Classes on classpath
        }
        return xmlTypeFactory;
    }
}
