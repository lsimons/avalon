/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 2002,2003 The Apache Software Foundation. All rights reserved.

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
 * @author <a href="mailto:mirceatoma@apache.org">Mircea Toma</a>
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
