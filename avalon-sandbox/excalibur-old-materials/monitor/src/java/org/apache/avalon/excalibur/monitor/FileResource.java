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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Managed File based Resource.  This is convenient when you want to dynamically
 * set and get the information from the resource.  For instance, the Resource does
 * not need to be actively monitored if all access to the resource goes through
 * this type of Resource.  It can notify the change as soon as the Writer or
 * OutputStream has been closed.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version $Id: FileResource.java,v 1.9 2003/03/22 12:46:50 leosimons Exp $
 */
public class FileResource
    extends StreamResource
{
    private final File m_file;

    /**
     * Instantiate the FileResource
     */
    public FileResource( final String resource )
        throws Exception
    {
        this( new File( resource ) );
    }

    public FileResource( final File resource )
        throws Exception
    {
        super( resource.getCanonicalPath() );
        m_file = resource;
        setPreviousModified( m_file.lastModified() );
    }

    /**
     * Determines the last time this resource was modified
     */
    public long lastModified()
    {
        return m_file.lastModified();
    }

    /**
     * Sets the resource value with an OutputStream
     */
    public InputStream getResourceAsStream()
        throws IOException
    {
        return new FileInputStream( m_file );
    }

    /**
     * Sets the resource value with a Writer
     */
    public Reader getResourceAsReader()
        throws IOException
    {
        return new FileReader( m_file );
    }

    /**
     * Sets the resource value with an OutputStream
     */
    public OutputStream setResourceAsStream()
        throws IOException
    {
        return new ResourceOutputStream( new FileOutputStream( m_file ), this );
    }

    /**
     * Sets the resource value with a Writer
     */
    public Writer setResourceAsWriter()
        throws IOException
    {
        return new ResourceWriter( new FileWriter( m_file ), this );
    }
}
