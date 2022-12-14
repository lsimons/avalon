/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.6 $ $Date: 2004/04/26 10:23:06 $
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
            if( name.endsWith( m_extensions[i] ) )
            {
                return true;
            }
        }
        return false;
    }
}


