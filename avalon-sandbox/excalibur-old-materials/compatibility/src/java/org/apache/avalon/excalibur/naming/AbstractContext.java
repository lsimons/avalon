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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * Abstract JNDI Context that can be inherited from to
 * provide a particular type of Context.
 *
 * @author Peter Donald
 * @version $Revision: 1.2 $
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/jndikit/
 */
public abstract class AbstractContext
    implements Context
{
    private Hashtable m_environment;

    public AbstractContext()
    {
        this( new Hashtable() );
    }

    public AbstractContext( final Hashtable environment )
    {
        m_environment = environment;
    }

    protected abstract NameParser getNameParser()
        throws NamingException;

    /**
     * Add a key-value pair to environment
     *
     * @param key the key
     * @param value the value
     * @return the value
     */
    public Object addToEnvironment( final String key, final Object value )
        throws NamingException
    {
        if( null == m_environment ) m_environment = new Hashtable( 5, 0.75f );
        return m_environment.put( key, value );
    }

    /**
     * Release resources associated with context.
     *
     */
    public void close()
    {
        m_environment = null;
    }

    protected boolean isSelf( final Name name )
    {
        return ( name.isEmpty() || name.get( 0 ).equals( "" ) );
    }

    /**
     * Bind an object to a name.
     *
     * @param name the name to bind to
     * @param object the object
     * @exception NamingException if an error occurs such as bad name or invalid binding
     */
    public void bind( final String name, final Object object )
        throws NamingException
    {
        bind( getNameParser().parse( name ), object );
    }

    /**
     * Bind an object to a name.
     *
     * @param name the name to bind to
     * @param object the object
     * @exception NamingException if an error occurs such as bad name or invalid binding
     */
    public void bind( final Name name, final Object object )
        throws NamingException
    {
        bind( name, object, false );
    }

    /**
     * Helper method to bind
     */
    protected abstract void bind( Name name, Object object, boolean rebind )
        throws NamingException;

    /**
     * Compose a name form a name and a prefix.
     *
     * @param name the name
     * @param prefix the prefix
     * @return the composed name
     * @exception NamingException if a badly formatted name for context
     */
    public String composeName( final String name, final String prefix )
        throws NamingException
    {
        final NameParser nameParser = getNameParser();
        final Name result =
            composeName( nameParser.parse( name ), nameParser.parse( prefix ) );
        return result.toString();
    }

    /**
     * Compose a name form a name and a prefix.
     *
     * @param name the name
     * @param prefix the prefix
     * @return the composed name
     * @exception NamingException if a badly formatted name for context
     */
    public Name composeName( final Name name, final Name prefix )
        throws NamingException
    {
        final Name result = (Name)( prefix.clone() );
        result.addAll( name );
        return result;
    }

    /**
     * Create a Subcontext.
     *
     * @param name the name of subcontext
     * @return the created context
     * @exception NamingException if an error occurs (ie context exists, badly formated name etc)
     */
    public Context createSubcontext( final String name )
        throws NamingException
    {
        return createSubcontext( getNameParser().parse( name ) );
    }

    /**
     * Destroy a Subcontext.
     *
     * @param name the name of subcontext to destroy
     * @exception NamingException if an error occurs such as malformed name or
     *            context not exiting or not empty
     */
    public void destroySubcontext( final String name )
        throws NamingException
    {
        destroySubcontext( getNameParser().parse( name ) );
    }

    /**
     * Return a copy of environment.
     *
     * @return the environment
     */
    public Hashtable getEnvironment()
        throws NamingException
    {
        if( null == m_environment )
            return new Hashtable( 3, 0.75f );
        else
            return (Hashtable)m_environment.clone();
    }

    /**
     * Get the NameParser for the named context.
     *
     * @param name
     * @return the NameParser
     * @exception NamingException if an error occurs
     */
    public NameParser getNameParser( final String name )
        throws NamingException
    {
        return getNameParser( getNameParser().parse( name ) );
    }

    /**
     * Get the NameParser for the named context.
     *
     * @param name
     * @return the NameParser
     * @exception NamingException if an error occurs
     */
    public NameParser getNameParser( final Name name )
        throws NamingException
    {
        if( name.isEmpty() )
        {
            return getNameParser();
        }

        Object object = lookup( name );
        if( !( object instanceof Context ) )
        {
            object = lookup( getPathName( name ) );
        }

        final Context context = (Context)object;
        final NameParser parser = context.getNameParser( "" );
        context.close();
        return parser;
    }

    /**
     * Enumerates the names bound in the named context, along with the objects bound to them.
     *
     * @param name the name of the context
     * @return the enumeration
     * @exception NamingException if an error occurs
     */
    public NamingEnumeration list( final String name )
        throws NamingException
    {
        return list( getNameParser().parse( name ) );
    }

    /**
     * Enumerates the names bound in the named context, along with the objects bound to them.
     *
     * @param name the name of the context
     * @return the enumeration
     * @exception NamingException if an error occurs
     */
    public NamingEnumeration listBindings( final String name )
        throws NamingException
    {
        return listBindings( getNameParser().parse( name ) );
    }

    /**
     * Get the object named.
     *
     * @param name the name
     * @return the object
     * @exception NamingException if an error occurs (ie object name is inavlid or unbound)
     */
    public Object lookup( final String name )
        throws NamingException
    {
        return lookup( getNameParser().parse( name ) );
    }

    /**
     * Get the object named following all links.
     *
     * @param name the name
     * @return the object
     * @exception NamingException if an error occurs (ie object name is inavlid or unbound)
     */
    public Object lookupLink( final String name )
        throws NamingException
    {
        return lookupLink( getNameParser().parse( name ) );
    }

    /**
     * Get the object named following all links.
     *
     * @param name the name
     * @return the object
     * @exception NamingException if an error occurs (ie object name is inavlid or unbound)
     */
    public Object lookupLink( final Name name )
        throws NamingException
    {
        return lookup( name );
    }

    /**
     * Binds a name to an object, overwriting any existing binding.
     *
     * @param name the name
     * @param object the object
     * @exception NamingException if an error occurs
     */
    public void rebind( final String name, final Object object )
        throws NamingException
    {
        rebind( getNameParser().parse( name ), object );
    }

    /**
     * Binds a name to an object, overwriting any existing binding.
     *
     * @param name the name
     * @param object the object
     * @exception NamingException if an error occurs
     */
    public void rebind( final Name name, final Object object )
        throws NamingException
    {
        bind( name, object, true );
    }

    /**
     * Remove a key-value pair form environment and return it.
     *
     * @param key the key
     * @return the value
     */
    public Object removeFromEnvironment( final String key )
        throws NamingException
    {
        if( null == m_environment ) return null;
        return m_environment.remove( key );
    }

    /**
     * Rename a already bound object
     *
     * @param oldName the old name
     * @param newName the new name
     * @exception NamingException if an error occurs
     */
    public void rename( final String oldName, final String newName )
        throws NamingException
    {
        rename( getNameParser().parse( oldName ), getNameParser().parse( newName ) );
    }

    public void rename( final Name oldName, final Name newName )
        throws NamingException
    {
        if( isSelf( oldName ) || isSelf( newName ) )
        {
            throw new InvalidNameException( "Failed to rebind self" );
        }
        else if( oldName.equals( newName ) )
        {
            throw new InvalidNameException( "Failed to rebind identical names" );
        }

        bind( newName, lookup( oldName ) );
        unbind( oldName );
    }

    /**
     * Unbind a object from a name.
     *
     * @param name the name
     * @exception NamingException if an error occurs
     */
    public void unbind( final String name )
        throws NamingException
    {
        unbind( getNameParser().parse( name ) );
    }

    /**
     * Utility method to retrieve raw environment value.
     * This means that null will be returned if the value is null.
     *
     * @return the environment hashtable or null
     */
    protected final Hashtable getRawEnvironment()
    {
        return m_environment;
    }

    /**
     * Get name components minus leaf name component.
     *
     * @param name the name elements leading up to last element
     * @return the name
     * @exception NamingException if an error occurs
     */
    protected Name getPathName( final Name name )
        throws NamingException
    {
        return name.getPrefix( name.size() - 1 );
    }

    /**
     * Get leaf name component from specified Name object.
     *
     * @param name the name to retrieve leaf from
     * @return the leaf name component
     * @exception NamingException if an error occurs
     */
    protected Name getLeafName( final Name name )
        throws NamingException
    {
        return name.getSuffix( name.size() - 1 );
    }
}
