/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

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

package org.apache.avalon.composition.model.impl;

import org.apache.avalon.composition.model.ComponentContext;
import org.apache.avalon.composition.model.ModelRuntimeException;
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
 * @version $Revision: 1.3 $ $Date: 2004/01/13 11:41:26 $
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
