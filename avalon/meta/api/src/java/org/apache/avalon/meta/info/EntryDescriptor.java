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

package org.apache.avalon.meta.info;

import java.io.Serializable;

/**
 * A descriptor that describes a value that must be placed
 * in components Context. It contains information about;
 * <ul>
 *   <li>key: the key that component uses to look up entry</li>
 *   <li>classname: the class/interface of the entry</li>
 *   <li>isOptional: true if entry is optional rather than required</li>
 * </ul>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2003/10/17 02:03:06 $
 */
public final class EntryDescriptor
        implements Serializable
{
    /**
     * The name the component uses to lookup entry.
     */
    private final String m_key;

    /**
     * The class/interface of the Entry.
     */
    private final String m_classname;

    /**
     * True if entry is optional, false otherwise.
     */
    private final boolean m_optional;

    /**
     * Immutable state of the entry.
     */
    private final boolean m_volatile;

    /**
     * An alias to a key.
     */
    private final String m_alias;

    /**
     * Construct an non-volotile required Entry.
     * @param key the context entry key
     * @param classname the classname of the context entry
     * @exception NullPointerException if the key or type value are null
     */
    public EntryDescriptor( final String key,
                            final String classname ) throws NullPointerException
    {
        this( key, classname, false );
    }

    /**
     * Construct an non-volotile Entry.
     * @param key the context entry key
     * @param classname the classname of the context entry
     * @param optional TRUE if this is an optional entry
     * @exception NullPointerException if the key or type value are null
     */
    public EntryDescriptor( final String key,
                            final String classname,
                            final boolean optional ) throws NullPointerException
    {
        this( key, classname, optional, false );
    }

    /**
     * Construct an Entry.
     * @param key the context entry key
     * @param classname the classname of the context entry
     * @param optional TRUE if this is an optional entry
     * @param isVolatile TRUE if the entry is consider to be immutable
     * @exception NullPointerException if the key or type value are null
     */
    public EntryDescriptor( final String key,
                            final String classname,
                            final boolean optional, 
                            final boolean isVolatile ) throws NullPointerException
    {
        this( key, classname, optional, isVolatile, null );
    }

    /**
     * Construct an Entry.
     * @param key the context entry key
     * @param classname the classname of the context entry
     * @param optional TRUE if this is an optional entry
     * @param isVolatile TRUE if the entry is is volatile
     * @param alias an alternative key used by the component to reference the key
     * @exception NullPointerException if the key or type value are null
     */
    public EntryDescriptor( final String key,
                            final String classname,
                            final boolean optional, 
                            final boolean isVolatile,
                            final String alias ) throws NullPointerException
    {
        if ( null == key )
        {
            throw new NullPointerException( "key" );
        }
        if ( null == classname )
        {
            throw new NullPointerException( "classname" );
        }

        m_key = key;
        m_classname = classname;
        m_optional = optional;
        m_volatile = isVolatile;
        m_alias = alias;
    }

    /**
     * Return the key that Component uses to lookup entry.
     *
     * @return the key that Component uses to lookup entry.
     */
    public String getKey()
    {
        return m_key;
    }

    /**
     * Return the alias that Component uses to lookup the entry.
     * If no alias is declared, the standard lookup key will be 
     * returned.
     *
     * @return the alias to the key.
     */
    public String getAlias()
    {
        if( m_alias != null )
        {
            return m_alias;
        }
        else
        {
            return m_key;
        }
    }

    /**
     * Return the key type of value that is stored in Context.
     * 
     * @return the key type of value that is stored in Context.
     */
    public String getClassname()
    {
        return m_classname;
    }

    /**
     * Return true if entry is optional, false otherwise.
     *
     * @return true if entry is optional, false otherwise.
     */
    public boolean isOptional()
    {
        return m_optional;
    }

    /**
     * Return true if entry is required, false otherwise.
     *
     * @return true if entry is required, false otherwise.
     */
    public boolean isRequired()
    {
        return !isOptional();
    }

    /**
     * Return true if entry is volotile.
     *
     * @return the volatile state of the entry
     */
    public boolean isVolatile()
    {
        return m_volatile;
    }

   /**
    * Test is the supplied object is equal to this object.
    * @param other the object to compare with this instance
    * @return true if the object are equivalent
    */
    public boolean equals( Object other )
    {
        boolean isEqual = other instanceof EntryDescriptor;

        if ( isEqual )
        {
            EntryDescriptor entry = (EntryDescriptor) other;

            isEqual = isEqual && m_key.equals( entry.m_key );
            isEqual = isEqual && m_classname.equals( entry.m_classname );
            isEqual = isEqual && m_optional == entry.m_optional;
            isEqual = isEqual && m_volatile == entry.m_volatile;
            if ( null == m_alias )
            {
                isEqual = isEqual && null == entry.m_alias;
            }
            else
            {
                isEqual = isEqual && m_alias.equals( entry.m_alias );
            }
        }

        return isEqual;
    }

   /**
    * Return the hashcode for the object.
    * @return the hashcode value
    */
    public int hashCode()
    {
        int hash = super.hashCode();
        hash >>>= 13;
        hash ^= m_key.hashCode();
        hash >>>= 13;
        hash ^= m_classname.hashCode();
        hash >>>= 13;
        hash ^= ( null != m_alias ) ? m_alias.hashCode() : 0;
        hash >>>= 13;
        hash >>>= ( m_volatile ) ? 1 : 3;
        hash >>>= ( m_optional ) ? 1 : 3;

        return hash;
    }
}
