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
 * This filters filenames for a certain prefix.
 *
 * <p>Eg., to print all files and directories in the current directory whose name starts with</p>
 * <code>foo</code>:
 *
 * <pre>
 * File dir = new File(".");
 * String[] files = dir.list( new PrefixFileFilter("foo"));
 * for ( int i=0; i&lt;files.length; i++ )
 * {
 *     System.out.println(files[i]);
 * }
 * </pre>
 *
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.6 $ $Date: 2004/04/26 10:23:06 $
 * @since 4.0
 */
public class PrefixFileFilter
        implements FilenameFilter
{
    private String[] m_prefixs;

    public PrefixFileFilter( final String[] prefixs )
    {
        m_prefixs = prefixs;
    }

    public PrefixFileFilter( final String prefix )
    {
        m_prefixs = new String[]{prefix};
    }

    public boolean accept( final File file, final String name )
    {
        for( int i = 0; i < m_prefixs.length; i++ )
        {
            if( name.startsWith( m_prefixs[i] ) )
            {
                return true;
            }
        }
        return false;
    }
}
