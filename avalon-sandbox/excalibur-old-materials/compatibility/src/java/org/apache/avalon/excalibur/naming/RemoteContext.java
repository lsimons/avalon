/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
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
package org.apache.avalon.excalibur.naming;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.MarshalledObject;
import java.util.Hashtable;
import java.util.Iterator;

import javax.naming.Binding;
import javax.naming.CommunicationException;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;

/**
 * Context that hooks up to a remote source.
 *
 * @author Peter Donald
 * @version $Revision: 1.2 $
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/jndikit/
 */
public class RemoteContext
    extends AbstractContext
    implements Serializable
{
    public static final String NAMESPACE_NAME = "org.apache.avalon.excalibur.naming.Namespace/NAME";
    public static final String NAMESPACE = "org.apache.avalon.excalibur.naming.Namespace";
    public static final String NAMING_PROVIDER = "org.apache.avalon.excalibur.naming.NamingProvider";

    private transient NamingProvider m_provider;
    private transient NameParser m_nameParser;
    private transient Namespace m_namespace;

    private Name m_baseName;

    //for deserialisation
    public RemoteContext()
    {
    }

    public RemoteContext( final Hashtable environment, final Name baseName )
        throws NamingException
    {
        super( environment );
        m_baseName = baseName;
    }

    /**
     * Helper method to bind
     */
    protected void bind( final Name name, Object object, final boolean rebind )
        throws NamingException
    {
        if( isSelf( name ) )
        {
            throw new InvalidNameException( "Failed to bind self" );
        }

        String className = null;

        object = getNamespace().getStateToBind( object, name, this, getRawEnvironment() );

        if( object instanceof Reference )
        {
            className = ( (Reference)object ).getClassName();
        }
        else if( object instanceof Referenceable )
        {
            object = ( (Referenceable)object ).getReference();
            className = ( (Reference)object ).getClassName();
        }
        else
        {
            className = object.getClass().getName();

            try
            {
                object = new MarshalledObject( object );
            }
            catch( final IOException ioe )
            {
                throw new NamingException( "Only Reference, Referenceables and " +
                                           "Serializable objects can be bound " +
                                           "to context" );
            }
        }

        try
        {
            if( rebind )
            {
                getProvider().rebind( getAbsoluteName( name ), className, object );
            }
            else
            {
                getProvider().bind( getAbsoluteName( name ), className, object );
            }
        }
        catch( final Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * Release resources associated with context.
     */
    public void close()
    {
        super.close();
        m_namespace = null;
        m_provider = null;
    }

    /**
     * Create a Subcontext.
     *
     * @param name the name of subcontext
     * @return the created context
     * @exception NamingException if an error occurs (ie context exists, badly formated name etc)
     */
    public Context createSubcontext( final Name name )
        throws NamingException
    {
        if( isSelf( name ) )
        {
            throw new InvalidNameException( "Failed to create null subcontext" );
        }

        Context result = null;
        try
        {
            result = getProvider().createSubcontext( getAbsoluteName( name ) );
        }
        catch( final Exception e )
        {
            throw handleException( e );
        }

        fillInContext( result );

        return result;
    }

    public void destroySubcontext( final Name name )
        throws NamingException
    {
        if( isSelf( name ) )
        {
            throw new InvalidNameException( "Failed to destroy self" );
        }

        try
        {
            getProvider().destroySubcontext( getAbsoluteName( name ) );
        }
        catch( final Exception e )
        {
            throw handleException( e );
        }
    }

    public String getNameInNamespace()
        throws NamingException
    {
        return getAbsoluteName( getNameParser().parse( "" ) ).toString();
    }

    /**
     * Enumerates the names bound in the named context.
     *
     * @param name the name of the context
     * @return the enumeration
     * @exception NamingException if an error occurs
     */
    public NamingEnumeration list( final Name name )
        throws NamingException
    {
        try
        {
            final NameClassPair[] result = getProvider().list( getAbsoluteName( name ) );
            return new ArrayNamingEnumeration( this, m_namespace, result );
        }
        catch( final Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * Enumerates the names bound in the named context, along with the objects bound to them.
     *
     * @param name the name of the context
     * @return the enumeration
     * @exception NamingException if an error occurs
     */
    public NamingEnumeration listBindings( final Name name )
        throws NamingException
    {
        try
        {
            final Binding[] result = getProvider().listBindings( getAbsoluteName( name ) );

            for( int i = 0; i < result.length; i++ )
            {
                final Object object = result[ i ].getObject();
                if( object instanceof Context )
                {
                    fillInContext( (Context)object );
                }
            }

            return new ArrayNamingEnumeration( this, m_namespace, result );
        }
        catch( final Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * Get the object named.
     *
     * @param name the name
     * @return the object
     * @exception NamingException if an error occurs (ie object name is inavlid or unbound)
     */
    public Object lookup( final Name name )
        throws NamingException
    {
        if( isSelf( name ) )
        {
            return new RemoteContext( getRawEnvironment(), m_baseName );
        }

        //TODO: actually do a real-lookup
        Object object = null;
        try
        {
            object = getProvider().lookup( getAbsoluteName( name ) );

            if( object instanceof MarshalledObject )
            {
                object = ( (MarshalledObject)object ).get();
            }

            object = getNamespace().getObjectInstance( object, name, this, getRawEnvironment() );

            if( object instanceof Context )
            {
                fillInContext( (Context)object );
            }
        }
        catch( final Exception e )
        {
            throw handleException( e );
        }

        return object;
    }

    /**
     * Unbind a object from a name.
     *
     * @param name the name
     * @exception NamingException if an error occurs
     */
    public void unbind( final Name name )
        throws NamingException
    {
        if( isSelf( name ) )
        {
            throw new InvalidNameException( "Failed to unbind self" );
        }

        try
        {
            getProvider().unbind( getAbsoluteName( name ) );
        }
        catch( final Exception e )
        {
            throw handleException( e );
        }
    }

    protected void fillInContext( final Context object )
        throws NamingException
    {
        final Hashtable environment = getRawEnvironment();
        final Iterator keys = environment.keySet().iterator();

        while( keys.hasNext() )
        {
            final String key = (String)keys.next();
            final Object value = environment.get( key );
            object.addToEnvironment( key, value );
        }
    }

    protected Namespace getNamespace()
        throws NamingException
    {
        if( null == m_namespace )
        {
            final Object object = getRawEnvironment().get( RemoteContext.NAMESPACE );

            if( !( object instanceof Namespace ) || null == object )
            {
                throw new ConfigurationException( "Context does not contain Namespace" );
            }
            else
            {
                m_namespace = (Namespace)object;
            }
        }

        return m_namespace;
    }

    protected NamingProvider getProvider()
        throws NamingException
    {
        if( null == m_provider )
        {
            final Object object = getRawEnvironment().get( RemoteContext.NAMING_PROVIDER );

            if( !( object instanceof NamingProvider ) || null == object )
            {
                throw new ConfigurationException( "Context does not contain provider" );
            }
            else
            {
                m_provider = (NamingProvider)object;
            }
        }

        return m_provider;
    }

    protected NameParser getNameParser()
        throws NamingException
    {
        if( null == m_nameParser )
        {
            //Make sure provider is valid and returns nameparser
            try
            {
                m_nameParser = getProvider().getNameParser();
            }
            catch( final Exception e )
            {
                throw handleException( e );
            }

        }
        return m_nameParser;
    }

    protected Name getAbsoluteName( final Name name )
        throws NamingException
    {
        return composeName( name, m_baseName );
    }

    protected NamingException handleException( final Exception e )
    {
        if( e instanceof NamingException )
        {
            return (NamingException)e;
        }
        else
        {
            return new CommunicationException( e.toString() );
        }
    }
}
