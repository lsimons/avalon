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

package org.apache.avalon.repository.main;

import java.util.Map;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.RepositoryException;
import org.apache.avalon.repository.RepositoryRuntimeException;
import org.apache.avalon.repository.meta.FactoryDescriptor;
import org.apache.avalon.repository.provider.Registry;

/**
 * A registry of available factory artifacts.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $
 */
public class DefaultRegistry implements Registry
{
    //------------------------------------------------------------------
    // immutable state 
    //------------------------------------------------------------------

   /**
    * System repository established by the intial context.
    */
    private final Repository m_repository;

   /**
    * A table of interfaces keys mapping to lists of supporting artifacts.
    */
    private final Map m_registry = new Hashtable();

   /**
    * A list of registered factory descriptors.
    */
    private final List m_descriptors = new ArrayList();

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    /**
     * Creates an initial repository context.
     *
     * @param repository the repository backing the registry
     * @param candidates factory artifact sequence for registration
     * @throws RepositoryException if an error occurs during establishment
     */
    DefaultRegistry( 
      Repository repository, Artifact[] candidates ) 
      throws RepositoryException
    {
        if( null == repository ) throw new NullPointerException( "repository" ); 
        if( null == candidates ) throw new NullPointerException( "candidates" ); 

        m_repository = repository;

        setupRegistry( candidates );
    }

    // ------------------------------------------------------------------------
    // Registry
    // ------------------------------------------------------------------------

    public Artifact[] getCandidates( Class service )
    {
        ArrayList list = new ArrayList();
        String classname = service.getName();
        FactoryDescriptor[] descriptors = getFactoryDescriptors();
        for( int i=0; i<descriptors.length; i++ )
        {
            FactoryDescriptor descriptor = descriptors[i];
            final String key = descriptor.getInterface();
            if( classname.equals( key ) )
            {
                list.add( descriptor.getArtifact() );
            }
        }
        return (Artifact[]) list.toArray( new Artifact[0] );
    }

    // ------------------------------------------------------------------------
    // private
    // ------------------------------------------------------------------------

    private void setupRegistry( Artifact[] artifacts ) throws RepositoryException
    {
        for( int i=0; i<artifacts.length; i++ )
        {
            Artifact artifact = artifacts[i];
            registerArtifact( artifact );
        }
    }

    private void registerArtifact( Artifact artifact ) throws RepositoryException
    {
        Attributes attributes = m_repository.getAttributes( artifact );
        FactoryDescriptor descriptor = new FactoryDescriptor( attributes );
        final String key = descriptor.getInterface();
        if( null == key ) 
        {
            final String error = 
              "Artifact [" + artifact + "] does not declare a exported interface.";
            throw new RepositoryException( error );
        }
        else if( !m_descriptors.contains( descriptor ) )
        {
            m_descriptors.add( descriptor );
        }
    }

    private FactoryDescriptor[] getFactoryDescriptors()
    {
        return (FactoryDescriptor[]) 
          m_descriptors.toArray( new FactoryDescriptor[0] );
    }
}
