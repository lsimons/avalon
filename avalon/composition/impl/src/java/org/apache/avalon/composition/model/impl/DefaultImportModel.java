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

import java.util.Map;

import org.apache.avalon.composition.model.ModelException;
import org.apache.avalon.composition.model.ComponentContext;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.composition.data.ImportDirective;
import org.apache.avalon.meta.info.EntryDescriptor;



/**
 * Default implementation of a the context entry import model.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2.2.1 $ $Date: 2004/01/04 17:23:17 $
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
