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
package org.apache.avalon.excalibur.io;

import java.io.File;
import java.io.FilenameFilter;

/**
 * This filters files based on the extension (what the filename
 * ends with). This is used in retrieving all the files of a
 * particular type.
 *
 * <p>Eg., to retrieve and print all <code>*.java</code> files in the current directory:</p>
 *
 * <pre>
 * File dir = new File(".");
 * String[] files = dir.list( new ExtensionFileFilter( new String[]{"java"} ) );
 * for (int i=0; i&lt;files.length; i++)
 * {
 *     System.out.println(files[i]);
 * }
 * </pre>
 *
 * @author  Federico Barbieri <fede@apache.org>
 * @author Serge Knystautas <sergek@lokitech.com>
 * @author Peter Donald
 * @version CVS $Revision: 1.5 $ $Date: 2003/12/05 15:15:13 $
 * @since 4.0
 */
public class ExtensionFileFilter
    implements FilenameFilter
{
    private String[] m_extensions;

    public ExtensionFileFilter( final String[] extensions )
    {
        m_extensions = extensions;
    }

    public ExtensionFileFilter( final String extension )
    {
        m_extensions = new String[]{extension};
    }

    public boolean accept( final File file, final String name )
    {
        for( int i = 0; i < m_extensions.length; i++ )
        {
            if( name.endsWith( m_extensions[ i ] ) )
            {
                return true;
            }
        }
        return false;
    }
}


