/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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

package org.apache.avalon.phoenix.framework.info;

/**
 * A descriptor describing the schema to validate the components
 * {@link org.apache.avalon.framework.parameters.Parameters} or
 * {@link org.apache.avalon.framework.configuration.Configuration}
 * object. If a component is neither
 * {@link org.apache.avalon.framework.parameters.Parameterizable}
 * nor {@link org.apache.avalon.framework.configuration.Configurable}
 * then this descriptor will hold empty values for location, category
 * and type.
 *
 * <p>Associated with each Schema is a set of arbitrary
 * Attributes that can be used to store extra information
 * about Schema requirements.</p>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003/03/22 12:07:13 $
 */
public class SchemaDescriptor
    extends FeatureDescriptor
{
    /**
     * The location of schema relative to component.
     */
    private final String m_location;

    /**
     * The type of the schema.
     */
    private final String m_type;

    /**
     * Create a Schema descriptor.
     *
     * @param location the location of schema relative to component
     * @param type the type of the schema
     * @param attributes the attributes associated with schema
     */
    public SchemaDescriptor( final String location,
                             final String type,
                             final Attribute[] attributes )
    {
        super( attributes );
        if( null == location )
        {
            throw new NullPointerException( "location" );
        }
        if( null == type )
        {
            throw new NullPointerException( "type" );
        }

        m_location = location;
        m_type = type;
    }

    /**
     * Return the location of the schema relative to the component.
     *
     * @return the location of the schema relative to the component.
     */
    public String getLocation()
    {
        return m_location;
    }

    /**
     * Return the type of the schema.
     * Usually represented as a URI referring to schema
     * namespace declaration.
     *
     * @return the type of the schema
     */
    public String getType()
    {
        return m_type;
    }
}
