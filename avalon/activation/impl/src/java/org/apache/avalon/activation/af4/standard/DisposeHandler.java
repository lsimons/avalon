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

package org.apache.avalon.activation.af4.standard;

import org.apache.avalon.activation.appliance.Engine;

import org.apache.avalon.activation.lifecycle.DestructionException;
import org.apache.avalon.activation.lifecycle.LifecycleDestroyExtension;

import org.apache.avalon.composition.model.ComponentModel;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.activity.Disposable;

import org.apache.avalon.meta.info.StageDescriptor;

public class DisposeHandler extends AbstractLogEnabled
    implements LifecycleDestroyExtension
{
    private static final Resources REZ =
      ResourceManager.getPackageResources( DisposeHandler.class );

    /**
     * Invocation of the deployment creation stage extension.
     * @param model the model representing the object under deployment
     * @param stage the extension stage descriptor
     * @param object the object under deployment
     * @exception if a deployment error occurs
     */
    public void destroy( ComponentModel model, Engine engine, StageDescriptor stage, Object instance)
      throws DestructionException
    {
        if( instance instanceof Disposable )
        {
            if( getLogger().isDebugEnabled() )
            {
                int id = System.identityHashCode( instance );
                getLogger().debug( "disposing of: " + id );
            }
            try
            {
                ((Disposable)instance).dispose();
            }
            catch( Throwable e )
            {
                final String error = 
                  REZ.getString( "lifecycle.dispose.component.warn" );
                getLogger().warn( error, e );
            }
        }
    }
} 
