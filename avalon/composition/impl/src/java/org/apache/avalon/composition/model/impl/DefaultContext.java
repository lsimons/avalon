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

import org.apache.avalon.composition.model.ModelRuntimeException;
import org.apache.avalon.composition.provider.ComponentContext;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

/**
 * <p>Default implementation of a context object.  The implementation
 * maintains a mapping between context keys and context entry models.
 * Requests for a context entry value are resolved through redirecting
 * the request to an assigned model.</p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.5 $ $Date: 2004/02/10 16:23:33 $
 */
public final class DefaultContext implements Context
{
    //==============================================================
    // static
    //==============================================================

    private static final Resources REZ =
      ResourceManager.getPackageResources( DefaultContext.class );

    //==============================================================
    // immutable state
    //==============================================================

    private final ComponentContext m_context;

    //==============================================================
    // constructor
    //==============================================================

   /**
    * <p>Creation of a new default context.</p>
    *
    * @param context the deployment context
    */
    public DefaultContext( ComponentContext context )
    {
        m_context = context;
    }
    
    //==============================================================
    // Context
    //==============================================================

   /**
    * Return a context value relative to a key. If the context entry
    * is unknown a {@link ContextException} containing the key as 
    * as the exception message and a null cause will be thrown.  If 
    * the contrext entry is recognized and a error occurs during 
    * value resolvution a {@link ContextException} will be thrown 
    * containing the causal exception.
    * 
    * @param key the context entry key
    * @return the context entry value
    * @exception ContextException if the key is unknown or unresolvable
    */
    public Object get( final Object key ) throws ContextException
    {
        try
        {
            return m_context.resolve( key.toString() );
        }
        catch( ModelRuntimeException e )
        {
            final String error = 
              REZ.getString( "context.entry.model.error", key );
            throw new ContextException( error, e );
        }
    }
}
