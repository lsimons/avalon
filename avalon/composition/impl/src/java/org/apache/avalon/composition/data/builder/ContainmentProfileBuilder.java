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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.meta.ConfigurationBuilder;
import org.apache.avalon.composition.data.Profile;
import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.composition.data.DeploymentProfile;
import org.apache.avalon.composition.data.MetaDataException;
import org.apache.avalon.composition.data.MetaDataRuntimeException;

import org.xml.sax.InputSource;


/**
 * A ContainmentProfileBuilder is responsible for building {@link ContainmentProfile}
 * objects from a configuration object.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2003/10/04 11:53:04 $
 */
public final class ContainmentProfileBuilder implements ContainmentProfileCreator
{

    private static final Resources REZ =
        ResourceManager.getPackageResources( ContainmentProfileBuilder.class );

    private XMLContainmentProfileCreator m_xml = 
      new XMLContainmentProfileCreator( );

    private final SerializedContainmentProfileCreator m_serial =
      new SerializedContainmentProfileCreator();

    /**
     * Create a {@link ContainmentProfile} from a stream.
     *
     * @param inputStream the stream that the resource is loaded from
     * @return the containment profile
     * @exception Exception if a error occurs during profile creation
     */
    public ContainmentProfile createContainmentProfile( InputStream inputStream )
        throws Exception
    {
        // we backup the inputstream content in a bytearray
        final byte[] buffer = new byte[1024];
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for( int read = 0; read >= 0; )
        {
            baos.write( buffer, 0, read );
            read = inputStream.read( buffer );
        }

        inputStream = new ByteArrayInputStream( baos.toByteArray() );
        
        try
        {
            final ContainmentProfile profile = 
              buildFromSerDescriptor( inputStream);
            if( null != profile )
            {
                return profile;
            }
        }
        catch( Throwable e )
        {
            // exception case here is not clear - basically we get a 
            // java.io.StreamCorruptedException if wthe input stream is 
            // referencing an XML stream - for the moment just go ahead
            // and see if we can resolve the source as an XML input but 
            // sooner or later we will need to update the serialized 
            // to return null if the source isn' a serialized source

            inputStream = new ByteArrayInputStream( baos.toByteArray() );

        }
        
        return buildFromXMLDescriptor( inputStream );
    }

    /**
     * Build ContainmentProfile from the serialized format.
     *
     * @throws Exception if an error occurs
     */
    private ContainmentProfile buildFromSerDescriptor( InputStream inputStream )
        throws Exception
    {
        return m_serial.createContainmentProfile( inputStream );
    }

    /**
     * Build ContainmentProfile from an XML descriptor.
     * @param stream the input stream
     * @throws Exception if an error occurs
     */
    private ContainmentProfile buildFromXMLDescriptor( InputStream stream )
        throws Exception
    {
        final InputSource source = new InputSource( stream );
        Configuration config = ConfigurationBuilder.build( source );
        return m_xml.createContainmentProfile( config );
    }

}
