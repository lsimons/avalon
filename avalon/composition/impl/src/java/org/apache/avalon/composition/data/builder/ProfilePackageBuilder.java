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

package org.apache.avalon.composition.data.builder;

import java.io.InputStream;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.meta.ConfigurationBuilder;
import org.apache.avalon.composition.data.ProfilePackage;
import org.apache.avalon.composition.data.ComponentProfile;

import org.xml.sax.InputSource;


/**
 * A ProfileBuilder is responsible for building a {@link ProfilePackage}
 * object from a source.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2.2.1 $ $Date: 2004/01/09 20:29:49 $
 */
public final class ProfilePackageBuilder implements ProfilePackageCreator
{
    private XMLProfilePackageCreator m_xml = 
      new XMLProfilePackageCreator( );

    private final SerializedProfilePackageCreator m_serial =
      new SerializedProfilePackageCreator();

    /**
     * Create a {@link ProfilePackage} from a type.
     *
     * @param name the component type name
     * @param clazz the component class
     * @return the profile package
     * @exception Exception if a error occurs during package creation
     */
    public ProfilePackage createProfilePackage( String name, Class clazz )
        throws Exception
    {
        ProfilePackage profiles = m_serial.createProfilePackage( name, clazz );
        if( profiles != null )
        {
            return profiles;
        }
        else
        {
            final String classname = clazz.getName();
            final String xprofile =
              classname.replace( '.', '/' ) + ".xprofile";
            final InputStream stream =
              clazz.getClassLoader().getResourceAsStream( xprofile );
            if( stream == null )
            {
                ComponentProfile profile = 
                  new ComponentProfile( name, classname );
                return new ProfilePackage( new ComponentProfile[]{ profile } );
            }
            else
            {
                return buildFromXMLDescriptor( name, classname, stream );
            }
        }
    }

    /**
     * Build ProfilePackage from the serialized format.
     *
     * @param input the object input stream
     * @throws Exception if an error occurs
     */
    private ProfilePackage buildFromSerDescriptor( InputStream input )
        throws Exception
    {
        return m_serial.createProfilePackage( input );
    }

    /**
     * Build a ProfilePackage from an XML descriptor.
     *
     * @param name the type name
     * @param classname the type classname
     * @param input the input stream for the profile
     * @throws Exception if an error occurs
     */
    private ProfilePackage buildFromXMLDescriptor( 
      final String name, final String classname, InputStream input )
        throws Exception
    {
        final InputSource source = new InputSource( input );
        Configuration config = ConfigurationBuilder.build( source );
        return m_xml.createProfilePackage( name, classname, config );
    }

}
