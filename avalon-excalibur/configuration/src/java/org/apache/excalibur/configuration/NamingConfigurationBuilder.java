/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.excalibur.configuration;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

/**
 * A NamingConfigurationBuilder builds <code>Configuration</code>s from JNDI or
 * LDAP directory trees.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public class NamingConfigurationBuilder
{
    private final boolean m_enableNamespaces;

    /**
     * Create a Configuration Builder that ignores namespaces.
     */
    public NamingConfigurationBuilder()
    {
        this( false );
    }

    /**
     * Create a Configuration Builder, specifying a flag that determines
     * namespace support.
     *
     * @param enableNamespaces If <code>true</code>, a configuration with
     * namespace information is built.
     */
    public NamingConfigurationBuilder( final boolean enableNamespaces )
    {
        m_enableNamespaces = enableNamespaces;
    }

    /**
     * Build a configuration object using an URI
     */
    public Configuration build( final String uri ) throws NamingException
    {
        final Hashtable env = new Hashtable();
        env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
        env.put( Context.SECURITY_AUTHENTICATION, "none" );
        env.put( Context.PROVIDER_URL, uri );

        final DirContext context = new InitialDirContext( env );

        return build( context );
    }

    /**
     * Build a configuration object using a naming context.
     */
    public Configuration build( final Context context ) throws NamingException
    {
        final DefaultConfiguration configuration;

        final String absoluteName = context.getNameInNamespace();
        final NameParser parser = context.getNameParser( absoluteName );
        final Name parsedName = parser.parse( absoluteName );

        String name = absoluteName;
        String prefix = "";
        //if composite name, use only the relative name.
        final int position = parsedName.size();
        if( position > 0 )
        {
            name = parsedName.get( position - 1 );
        }

        if( context instanceof DirContext )
        {
            //extract element name, and namespace prefix
            final Attributes attrs = ( (DirContext)context ).getAttributes( "" );

            final NamingEnumeration attributes = attrs.getAll();
            while( attributes.hasMore() )
            {
                final Attribute attribute = (Attribute)attributes.next();
                final String id = attribute.getID();
                if( name.startsWith( id ) )
                {
                    name = (String)attribute.get();
                    if( m_enableNamespaces ) prefix = id;
                    attrs.remove( id );
                    break;
                }
            }

            configuration = new DefaultConfiguration( name, null, "", prefix );
            copyAttributes( attrs, configuration );
        }
        else
            configuration = new DefaultConfiguration( name, null, "", prefix );

        final NamingEnumeration bindings = context.listBindings( "" );
        while( bindings.hasMore() )
        {
            final Binding binding = (Binding)bindings.next();
            final Object object = binding.getObject();

            if( ( object instanceof Number ) ||
                ( object instanceof String ) )
            {
                configuration.setValue( object.toString() );
            }

            if( object instanceof Context )
            {
                final Context child = (Context)object;
                configuration.addChild( build( child ) );
            }
        }

        return configuration;
    }

    private void copyAttributes( final Attributes attrs, final DefaultConfiguration configuration ) throws NamingException
    {
        final NamingEnumeration attributes = attrs.getAll();
        while( attributes.hasMore() )
        {
            final Attribute attribute = (Attribute)attributes.next();
            final String attrName = attribute.getID();
            final Object attrValue = attribute.get();
            configuration.setAttribute( attrName, attrValue.toString() );
        }
    }
}
