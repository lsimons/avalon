/* 
 * Copyright 2004 Apache Software Foundation
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

package org.apache.avalon.meta.info.writer;

import java.io.OutputStream;

import org.apache.avalon.meta.info.Service;

/**
 * Interface implemented by objects supporting the writing
 * of a {@link Service} to an output stream.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/02/21 13:27:04 $
 */
public interface ServiceWriter
{
    /**
     * Write a {@link Service} to a stream
     *
     * @param service the meta Service instance
     * @param stream the destination stream
     * @throws Exception if an error occurs while writing to the stream
     */
    void writeService( Service service, OutputStream stream )
        throws Exception;
}
