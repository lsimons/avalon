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

import java.util.Map;

import org.apache.avalon.composition.model.ModelException;
import org.apache.avalon.composition.provider.ComponentContext;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.composition.data.ImportDirective;

import org.apache.avalon.meta.info.EntryDescriptor;



/**
 * Default implementation of a the context entry import model.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.5 $ $Date: 2004/02/10 16:23:33 $
 */
public class DefaultImportModel extends DefaultEntryModel
{
    //==============================================================
    // static
    //==============================================================

    private static final Resources REZ =
      ResourceManager.getPackageResources( DefaultImportModel.class );

    //==============================================================
    // immutable state
    //==============================================================

    private final ImportDirective m_directive;

    private final EntryDescriptor m_descriptor;

    private final ComponentContext m_context;

    private final Map m_map;

    //==============================================================
    // mutable state
    //==============================================================

    private Object m_value;

    //==============================================================
    // constructor
    //==============================================================

   /**
    * Creation of a new context entry import model.
    *
    * @param descriptor the context entry descriptor
    * @param directive the context entry directive
    * @param context the containment context
    */
    public DefaultImportModel( 
      EntryDescriptor descriptor, ImportDirective directive, 
      ComponentContext context, Map map )
    {
        super( descriptor );
        if( directive == null )
        {
            throw new NullPointerException( "directive" );
        }
        if( context == null )
        {
            throw new NullPointerException( "context" );
        }
        m_descriptor = descriptor;
        m_directive = directive;
        m_context = context;
        m_map = map;
    }

    //==============================================================
    // ContainmentContext
    //==============================================================

   /**
    * Return the context entry value.
    * 
    * @return the context entry value
    */
    public Object getValue() throws ModelException
    {
        if( m_value != null )
        {
            return m_value;
        }
        
        String target = m_descriptor.getKey();
        String key = m_directive.getImportKey();

        Object object = null;
        try
        {
            object = m_context.resolve( key );
        }
        catch( ContextException e )
        {
            object = m_map.get( key );
            if( object == null )
            {
                final String error = 
                  REZ.getString( 
                    "import.missing-entry.error", key, target );
                    throw new ModelException( error );
            }
        }

        //
        // validate the value before returning it
        // (should move this code up to the context model)
        //

        String classname = m_descriptor.getClassname();
        
        Class clazz = null;
        try
        {
            clazz = m_context.getClassLoader().loadClass( classname );
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( 
                "import.load.error", target, classname );
            throw new ModelException( error, e );
        }
        
        if( !( clazz.isAssignableFrom( object.getClass() ) ) )
        {
            final String error = 
              REZ.getString( 
                "import.type-conflict.error", key, classname, target );
            throw new ModelException( error );
        }

        if( !m_descriptor.isVolatile() )
        {
            m_value = object;
        }
        
        return object;
    }
}
