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
 * This takes a <code>FilenameFilter<code> as input and inverts the selection.
 * This is used in retrieving files that are not accepted by a filter.
 *
 * <p>
 * Eg., here is how one could use <code>InvertedFileFilter</code> in conjunction with
 * {@link org.apache.avalon.excalibur.io.ExtensionFileFilter} to print all files not ending in
 * <code>.bak</code> or <code>.BAK</code> in the current directory:
 * </p>
 *
 * <pre>
 * File dir = new File(".");
 * String[] files = dir.list(
 *     new InvertedFileFilter(
 *         new ExtensionFileFilter( new String[]{".bak", ".BAK"} )
 *         )
 *     );
 * for ( int i=0; i&lt;files.length; i++ )
 * {
 *     System.out.println(files[i]);
 * }
 * </pre>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2004/04/26 10:23:06 $
 * @since 4.0
 */
public class InvertedFileFilter
        implements FilenameFilter
{
    private final FilenameFilter m_originalFilter;

    public InvertedFileFilter( final FilenameFilter originalFilter )
    {
        m_originalFilter = originalFilter;
    }

    public boolean accept( final File file, final String name )
    {
        return !m_originalFilter.accept( file, name );
    }
}


