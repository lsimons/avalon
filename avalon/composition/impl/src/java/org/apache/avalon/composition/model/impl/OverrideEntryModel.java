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

package org.apache.avalon.composition.model.impl;

import org.apache.avalon.meta.info.EntryDescriptor;

/**
 * Utility class that enables assignment of an absolute value to a 
 * context entry.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/02/22 16:12:58 $
 */
public class OverrideEntryModel extends DefaultEntryModel
{
    //--------------------------------------------------------------
    // mutable state
    //--------------------------------------------------------------

    private Object m_value;

    //--------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------

   /**
    * Creation of a new overriding context entry.
    *
    * @param descriptor the context entry descriptor
    * @param object the value to return for the entry
    * @param context the containment context
    */
    public OverrideEntryModel( 
      EntryDescriptor descriptor, Object value ) throws ModelException
    {
        super( descriptor );
        if( value == null )
        {
            throw new NullPointerException( "value" );
        }
        m_value = value;
    }

    //--------------------------------------------------------------
    // EntryModel
    //--------------------------------------------------------------

   /**
    * Return the context entry value.
    * 
    * @return the context entry value
    */
    public Object getValue()
    {
        return m_value;
    }
}
