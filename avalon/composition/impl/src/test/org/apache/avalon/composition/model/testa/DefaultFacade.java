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

package org.apache.avalon.composition.model.testa;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;

public class DefaultFacade implements Facade
{
    private Context m_context;

    public DefaultFacade( Context context )
    {
        m_context = context;
    }

    //------------------------------------------------------------
    // Context
    //------------------------------------------------------------

    public Object get( final Object key ) throws ContextException
    {
        return m_context.get( key );
    }

    //------------------------------------------------------------
    // Extra
    //------------------------------------------------------------
    
    public String getName()
    {
        try
        {
            return (String) m_context.get( "urn:avalon:name" );
        }
        catch( ContextException e )
        {
            throw new RuntimeException( e.toString() );
        }
    }
}
