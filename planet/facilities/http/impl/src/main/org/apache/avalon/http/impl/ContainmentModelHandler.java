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

package org.apache.avalon.http.impl;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.avalon.composition.model.ComponentModel;

import org.mortbay.jetty.servlet.ServletHandler;

/**
 * The ContainmentModelHandler handles holders relative to a container
 * partition.
 *
 * WARNING - this class will be totally rewritten in conjunction with 
 * a context component.  The current version simply allows validation 
 * the HTTP request redirection.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/04/04 15:00:56 $
 */
class ContainmentModelHandler extends ServletHandler
{
    private final String m_partition;

    private Map m_models = new HashMap();
    private Map m_holders = new HashMap();

   /**
    * Construct a Servlet Handler using a supplied servlet holder.
    *
    * @param partition the partition that this handler handles
    */
    public ContainmentModelHandler( String partition )
    {
        super();
        m_partition = partition;
    }

    public void addComponentModel( ComponentModel model )
    {
        String name = model.getName();
        if( null == m_models.get( name ) )
        {
            m_models.put( name, model );
        }
        else
        {
            final String error = 
              "Duplicate name [" + name 
              + "] in partition handler [" 
              + m_partition + "].";
            throw new IllegalArgumentException( error );
        }
    }

   /** 
    * Initialize load-on-startup servlets.
    */
    public void initializeServlets()
      throws Exception
    {
        ComponentModel[] models = 
          (ComponentModel[]) m_models.values().toArray( new ComponentModel[0] );
        for( int i=0; i<models.length; i++ )
        {
            ComponentModel model = models[i];
            if( model.getActivationPolicy() )
            {
                ComponentModelHolder holder =
                    new ComponentModelHolder( this, model );
                m_holders.put( model.getName(), holder );
            }
        }
    }

   /**
    * Return a key/value pair holding the ServletHolder 
    * matching the supplied path.  This method gets called
    * for each http service request.
    *
    * @param path Path within context.
    * @return PathMap Entries pathspec to ServletHolder
    */
    public Map.Entry getHolderEntry( String path )
    {
        final String key = path.substring( 1 );
        Iterator iterator = m_holders.entrySet().iterator();
        while( iterator.hasNext() )
        {
            Map.Entry entry = (Map.Entry) iterator.next();
            if( entry.getKey().equals( key ) )
            {
                return entry;
            }  
        }
        
        //
        // its a new request for a lazy component
        //

        ComponentModel[] models = 
          (ComponentModel[]) m_models.values().toArray( new ComponentModel[0] );
        for( int i=0; i<models.length; i++ )
        {
            ComponentModel model = models[i];
            if( model.getName().equals( key ) )
            {
                ComponentModelHolder holder =
                    new ComponentModelHolder( this, model );
                m_holders.put( model.getName(), holder );
                return getHolderEntry( path );
            }
        }

        final String error = 
          "Unable to resolve component for path: " + path;
        throw new IllegalArgumentException( error );
    }
}
