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
package org.apache.avalon.excalibur.monitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceValidity;

/**
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version $Id: SourceResource.java,v 1.14 2003/03/22 12:46:50 leosimons Exp $
 */
public final class SourceResource
    extends StreamResource
{
    /** The wrapped source object */
    private final Source m_source;

    /** The last validity object */
    private SourceValidity m_validity;

    /**
     * Instantiate the SourceResource
     */
    public SourceResource( final Source source )
        throws Exception
    {
        super( source.getURI() );

        m_source = source;
        setPreviousModified( System.currentTimeMillis() );
        m_validity = source.getValidity();
    }

    /**
     * Determines the last time this resource was modified
     */
    public long lastModified()
    {
        if( null == m_validity )
        {
            return System.currentTimeMillis();
        }
        else
        {
            int valid = m_validity.isValid();
            boolean isValid = false;
            SourceValidity newVal = null;
            if ( valid == 0 ) {
                m_source.refresh();
                newVal = m_source.getValidity();
                if( newVal != null)
                {
                    valid = m_validity.isValid( newVal );
                    isValid = (valid == 1);
                }
            } else {
                isValid = (valid == 1);
            }
            if ( isValid ) {
                return getPreviousModified();
            } else {
                if ( null == newVal ) {
                    m_source.refresh();
                    m_validity = m_source.getValidity();
                }
                return System.currentTimeMillis();
            }
        }
    }

    /**
     * Sets the resource value with an OutputStream
     */
    public InputStream getResourceAsStream()
        throws IOException
    {
        try
        {
            return m_source.getInputStream();
        }
        catch( SourceException se )
        {
            throw new IOException( "SourceException: " + se.getMessage() );
        }
    }

    /**
     * Sets the resource value with a Writer
     */
    public Reader getResourceAsReader()
        throws IOException
    {
        return new InputStreamReader( getResourceAsStream() );
    }

    /**
     * Sets the resource value with an OutputStream
     */
    public OutputStream setResourceAsStream()
        throws IOException
    {
        throw new IOException( "setResourceAsStream() not supported for URLResource" );
    }

    /**
     * Sets the resource value with a Writer
     */
    public Writer setResourceAsWriter()
        throws IOException
    {
        throw new IOException( "setResourceAsWriter() not supported for URLResource" );
    }

    public Source getSource()
    {
        return m_source;
    }
}
