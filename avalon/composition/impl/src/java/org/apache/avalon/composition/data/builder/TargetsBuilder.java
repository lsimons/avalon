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
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.meta.ConfigurationBuilder;
import org.apache.avalon.composition.data.Targets;
import org.apache.avalon.composition.data.builder.TargetsCreator;

import org.xml.sax.InputSource;


/**
 * A TargetsBuilderis responsible for building {@link Targets}
 * objects from a configuration object.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2003/10/28 20:21:00 $
 */
public final class TargetsBuilder implements TargetsCreator
{

    private static final Resources REZ =
        ResourceManager.getPackageResources( TargetsBuilder.class );

    private XMLTargetsCreator m_xml = 
      new XMLTargetsCreator( );

    private final SerializedTargetsCreator m_serial =
      new SerializedTargetsCreator();


    /**
     * Create a {@link TargetDirective} from a stream.
     *
     * @param inputStream the stream that the resource is loaded from
     * @return the target directive
     * @exception Exception if a error occurs during directive creation
     */
    public Targets createTargets( InputStream inputStream )
        throws Exception
    {
        try
        {
            final Targets directive = buildFromSerDescriptor( inputStream);
            if( null != directive )
            {
                return directive;
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
        }

        return buildFromXMLDescriptor( inputStream );
    }

    /**
     * Build Targets from the serialized format.
     *
     * @throws Exception if an error occurs
     */
    private Targets buildFromSerDescriptor( InputStream inputStream )
        throws Exception
    {
        return m_serial.createTargets( inputStream );
    }

    /**
     * Build Targets from an XML descriptor.
     *
     * @throws Exception if an error occurs
     */
    private Targets buildFromXMLDescriptor( InputStream inputStream )
        throws Exception
    {
        final InputSource inputSource = new InputSource( inputStream );
        Configuration config = ConfigurationBuilder.build( inputSource );
        return m_xml.createTargets( config );
    }
}
