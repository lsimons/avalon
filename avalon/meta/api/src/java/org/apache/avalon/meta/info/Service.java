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
 * This class contains the meta information about a particular
 * service. It contains a set of attributes qualifying the service;
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 08:15:09 $
 */
public class Service extends Descriptor
{
    //=========================================================================
    // state
    //=========================================================================

    /**
     * The service reference.
     */
    private final ReferenceDescriptor m_reference;

    /**
     * The optional context entry criteria.
     */
    private final EntryDescriptor[] m_entries;

    //=========================================================================
    // constructor
    //=========================================================================

    /**
     * Creation of a new Service instance using a classname and
     * supplied properties argument.
     *
     * @param reference the versioned classname
     */
    public Service( final ReferenceDescriptor reference )
    {
        this( reference, null, null );
    }

    /**
     * Creation of a new Service instance using a classname and
     * supplied properties argument.
     *
     * @param reference the versioned classname
     * @param entries the set of attributes to assign to the descriptor
     */
    public Service(
            final ReferenceDescriptor reference,
            final EntryDescriptor[] entries )
    {
        this( reference, entries, null );
    }

    /**
     * Creation of a new Service instance using a classname and
     * supplied properties argument.
     *
     * @param reference the versioned classname
     * @param attributes the set of attributes to assign to the descriptor
     */
    public Service(
            final ReferenceDescriptor reference,
            final Properties attributes )
    {
        this( reference, null, attributes );
    }

    /**
     * Creation of a new Service instance using a classname and
     * supplied properties argument.
     *
     * @param reference the versioned classname
     * @param entries the set of optional context entries
     * @param attributes the set of attributes to assign to the descriptor
     */
    public Service(
            final ReferenceDescriptor reference,
            final EntryDescriptor[] entries,
            final Properties attributes )
    {
        super( attributes );
        if ( reference == null )
        {
            throw new NullPointerException( "reference" );
        }
        m_reference = reference;
        if ( entries == null )
        {
            m_entries = new EntryDescriptor[0];
        }
        else
        {
            m_entries = entries;
        }
    }

    //=========================================================================
    // implementation
    //=========================================================================

    /**
     * Return the service classname key.
     * @return the service classname
     */
    public String getClassname()
    {
        return m_reference.getClassname();
    }

    /**
     * Return the service version.
     * @return the version
     */
    public Version getVersion()
    {
        return m_reference.getVersion();
    }

    /**
     * Return the service reference.
     * @return the reference
     */
    public ReferenceDescriptor getReference()
    {
        return m_reference;
    }

    /**
     * Return the entries declared by the service.
     *
     * @return the entry descriptors
     */
    public EntryDescriptor[] getEntries()
    {
        return m_entries;
    }

    /**
     * Determine if supplied reference will match this service.
     * To match a service has to have same classname and must comply with version.
     *
     * @param reference the reference descriptor
     * @return true if matches, false otherwise
     */
    public boolean matches( final ReferenceDescriptor reference )
    {
        return m_reference.matches( reference );
    }

    /**
     * Return the hashcode for this service defintion.
     * @return the hashcode value
     */
    public int hashCode()
    {
        return m_reference.hashCode();
    }

    /**
     * Compare this object to the supplied object for equality.
     * @param other the object to compare to this object
     * @return true if this object matches the supplied object
     *    in terms of service classname and version
     */
    public boolean equals( Object other )
    {
        boolean match = false;

        if ( other instanceof ReferenceDescriptor )
        {
            match = matches( (ReferenceDescriptor) other );
        }
        else if ( other instanceof Service )
        {
            Service ref = (Service) other;
            match = ref.getClassname().equals( getClassname() )
                    && ref.getVersion().complies( getVersion() );
        }

        return match;
    }

    /**
     * Returns a string representation of the service.
     * @return a string representation
     */
    public String toString()
    {
        return getReference().toString();
    }

}
