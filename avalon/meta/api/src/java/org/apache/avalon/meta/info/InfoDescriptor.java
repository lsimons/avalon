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

import java.util.Properties;

import org.apache.avalon.framework.Version;

/**
 * This class is used to provide explicit information to assembler
 * and administrator about the Component. It includes information
 * such as;
 *
 * <ul>
 *   <li>a symbolic name</li>
 *   <li>classname</li>
 *   <li>version</li>
 * </ul>
 *
 * <p>The InfoDescriptor also includes an arbitrary set
 * of attributes about component. Usually these are container
 * specific attributes that can store arbitrary information.
 * The attributes should be stored with keys based on package
 * name of container.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $ $Date: 2003/10/19 10:29:57 $
 */
public final class InfoDescriptor extends Descriptor
{
    //-------------------------------------------------------------------
    // static
    //-------------------------------------------------------------------

    public static final String TRANSIENT = "transient";

    public static final String SINGLETON = "singleton";

    public static final String THREAD = "thread";

    public static final String POOLED = "pooled";

    public static final String LIBERAL_KEY = "liberal";
    public static final String DEMOCRAT_KEY = "democrat";
    public static final String CONSERVATIVE_KEY = "conservative";

    public static final int UNDEFINED = -1;
    public static final int LIBERAL = 0;
    public static final int DEMOCRAT = 1;
    public static final int CONSERVATIVE = 2;

    //-------------------------------------------------------------------
    // immutable state
    //-------------------------------------------------------------------

    /**
     * The short name of the Component Type. Useful for displaying
     * human readable strings describing the type in
     * assembly tools or generators.
     */
    private final String m_name;

    /**
     * The implementation classname.
     */
    private final String m_classname;

    /**
     * The version of component that descriptor describes.
     */
    private final Version m_version;

    /**
     * The component lifestyle.
     */
    private final String m_lifestyle;

    /**
     * The component configuration schema.
     */
    private final String m_schema;

    /**
     * The component garbage collection policy. The value returned is either 
     * LIBERAL, DEMOCAT or CONSERVATIVE.  A component implementing a LIBERAL policy 
     * will be decommissioned if no references exist.  A component declaring a 
     * DEMOCRAT policy will exist without reference so long as memory contention
     * does not occur.  A component implementing CONSERVATIVE policies will be 
     * maintained irrespective of usage and memory constraints so long as its 
     * scope exists (the jvm for a "singleton" and Thread for "thread" lifestyles).  
     * The default policy is CONSERVATIVE.
     */
    private final int m_collection;

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

    /**
     * Creation of a new component descriptor using a supplied name, key, version
     * and attribute set.
     *
     * @param classname the implemetation classname
     * @exception IllegalArgumentException if the implementation key is not a classname
     */
    public InfoDescriptor( final String classname )
            throws IllegalArgumentException
    {
        this( null, classname, null, null, null, null );
    }

    /**
     * Creation of a new info descriptor using a supplied name, key, version
     * and attribute set.
     *
     * @param name the component name
     * @param classname the implemetation classname
     * @param version the implementation version
     * @param attributes a set of attributes associated with the component type
     * @exception IllegalArgumentException if the implementation key is not a classname
     * @since 1.1
     */
    public InfoDescriptor( final String name,
                           final String classname,
                           final Version version,
                           final String lifestyle,
                           final String schema,
                           final Properties attributes )
            throws IllegalArgumentException
    {
        this( name, classname, version, lifestyle, null, schema, attributes );
    }

    /**
     * Creation of a new info descriptor using a supplied name, key, version
     * and attribute set.
     *
     * @param name the component name
     * @param classname the implemetation classname
     * @param version the implementation version
     * @param attributes a set of attributes associated with the component type
     * @exception IllegalArgumentException if the implementation key is not a classname
     * @since 1.2
     */
    public InfoDescriptor( final String name,
                           final String classname,
                           final Version version,
                           final String lifestyle,
                           final String collection,
                           final String schema,
                           final Properties attributes )
            throws IllegalArgumentException
    {
        super( attributes );

        if ( null == classname ) throw new NullPointerException( "classname" );

        if ( classname.indexOf( "/" ) > -1 )
        {
            throw new IllegalArgumentException( "classname: " + classname );
        }

        m_classname = classname;
        m_version = version;
        m_schema = schema;

        if ( lifestyle == null )
        {
            m_lifestyle = TRANSIENT;
        }
        else
        {
            validateLifestyle( lifestyle );
            m_lifestyle = lifestyle;
        }

        int p = getCollectionPolicy( collection );
        if( p > UNDEFINED )
        {
            m_collection = p;
        }
        else
        {
            m_collection = CONSERVATIVE;
        }

        if ( name != null )
        {
            m_name = name;
        }
        else
        {
            m_name = getClassName( classname );
        }
    }

    private void validateLifestyle( String lifestyle ) throws IllegalArgumentException
    {
        if ( lifestyle.equals( TRANSIENT )
                || lifestyle.equals( SINGLETON )
                || lifestyle.equals( THREAD )
                || lifestyle.equals( POOLED ) )
        {
            return;
        }
        final String error = "Lifestyle policy not recognized: " + lifestyle;
        throw new IllegalArgumentException( error );
    }

    private String getClassName( String classname )
    {
        int i = classname.lastIndexOf( "." );
        if ( i == -1 )
        {
            return classname.toLowerCase();
        }
        else
        {
            return classname.substring( i + 1, classname.length() ).toLowerCase();
        }
    }

    /**
     * Return the symbolic name of component.
     *
     * @return the symbolic name of component.
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Return the component termination policy as a String.
     *
     * @return the policy
     */
    public int getCollectionPolicy()
    {
        return m_collection;
    }

   /**
    * Test is the component type implements a liberal collection policy.
    *
    * @return the policy
    */
    public boolean isLiberal()
    {
        return m_collection == LIBERAL;
    }

    /**
     * Test is the component type implements a democrat collection policy.
     *
     * @return the policy
     */
    public boolean isDemocrat()
    {
        return m_collection == DEMOCRAT;
    }

    /**
     * Test is the component type implements a coservative collection policy.
     *
     * @return the policy
     */
    public boolean isConservative()
    {
        return m_collection == CONSERVATIVE;
    }

    /**
     * Return the configuration schema.
     *
     * @return the schema declaration (possibly null)
     */
    public String getConfigurationSchema()
    {
        return m_schema;
    }

    /**
     * Return the implementation class name for the component type.
     *
     * @return the implementation class name
     */
    public String getClassname()
    {
        return m_classname;
    }

    /**
     * Return the version of component.
     *
     * @return the version of component.
     */
    public Version getVersion()
    {
        return m_version;
    }

    /**
     * Return the component lifestyle.
     *
     * @return the lifestyle
     */
    public String getLifestyle()
    {
        return m_lifestyle;
    }

    /**
     * Return a string representation of the info descriptor.
     * @return the stringified type
     */
    public String toString()
    {
        return "[" + getName() + "] " + getClassname() + ":" + getVersion();
    }

   /**
    * Test is the supplied object is equal to this object.
    * @return true if the object are equivalent
    */
    public boolean equals(Object other)
    {
        boolean isEqual = super.equals(other) && other instanceof InfoDescriptor;

        if (isEqual)
        {
            InfoDescriptor info = (InfoDescriptor)other;
            isEqual = isEqual && m_classname.equals( info.m_classname );
            isEqual = isEqual && ( m_collection == info.m_collection );
            isEqual = isEqual && m_name.equals( info.m_name );
            isEqual = isEqual && m_lifestyle.equals( info.m_lifestyle );

            if ( null == m_version )
            {
                isEqual = isEqual && null == info.m_version;
            }
            else
            {
                isEqual = isEqual && m_version.equals(info.m_version);
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

        hash >>>= 7;
        hash ^= m_classname.hashCode();
        hash >>>= 7;

        if ( null != m_name )
        {
            hash >>>= 7;
            hash ^= m_name.hashCode();
        }

        if ( null != m_lifestyle )
        {
            hash >>>= 7;
            hash ^= m_lifestyle.hashCode();
        }

        if ( null != m_version )
        {
            hash >>>= 7;
            hash ^= m_version.hashCode();
        }

        return hash;
    }

    public static String getCollectionPolicyKey( int policy )
    {
        if ( policy == UNDEFINED )
        {
            return null;
        }
        else
        {
            if( policy == CONSERVATIVE )
            {
                return CONSERVATIVE_KEY;
            }
            else if( policy == DEMOCRAT )
            {
                return DEMOCRAT_KEY;
            }
            else if( policy == LIBERAL )
            {
                return LIBERAL_KEY;
            }
            else
            {
                final String error =
                  "Unrecognized collection argument [" + policy + "]";
                throw new IllegalArgumentException( error );
            }
        }
    }

    public static int getCollectionPolicy( String policy )
    {
        if ( policy == null )
        {
            return UNDEFINED;
        }
        else
        {
            if( policy.equalsIgnoreCase( CONSERVATIVE_KEY ) )
            {
                return CONSERVATIVE;
            }
            else if( policy.equalsIgnoreCase( DEMOCRAT_KEY ) )
            {
                return DEMOCRAT;
            }
            else if( policy.equalsIgnoreCase( LIBERAL_KEY ) )
            {
                return LIBERAL;
            }
            else
            {
                final String error =
                  "Unrecognized collection argument [" + policy + "]";
                throw new IllegalArgumentException( error );
            }
        }
    }

}
