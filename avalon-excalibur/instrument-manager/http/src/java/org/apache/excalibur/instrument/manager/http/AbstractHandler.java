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

package org.apache.excalibur.instrument.manager.http;

import org.apache.excalibur.instrument.manager.http.server.AbstractHTTPURLPrintWriterHandler;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2004/03/06 14:01:28 $
 * @since 4.1
 */
public abstract class AbstractHandler
    extends AbstractHTTPURLPrintWriterHandler
{
    /** The instrument manager */
    private InstrumentManagerClient m_manager;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new AbstractHandler.
     *
     * @param path The path handled by this handler.
     * @param contentType The content type.
     * @param manager Reference to the instrument manager client interface.
     */
    public AbstractHandler( String path,
                            String contentType,
                            InstrumentManagerClient manager )
    {
        super( path, contentType, InstrumentManagerHTTPConnector.ENCODING );
        
        m_manager = manager;
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Returns a reference to the instrument manager client interface.
     *
     * @return A reference to the instrument manager client interface. 
     */
    public InstrumentManagerClient getInstrumentManagerClient()
    {
        return m_manager;
    }
}

