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

package org.apache.avalon.composition.data.builder;

import java.util.ArrayList;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.composition.data.DeploymentProfile;
import org.apache.avalon.composition.data.DependencyDirective;
import org.apache.avalon.composition.data.SelectionDirective;
import org.apache.avalon.composition.data.*;
import org.apache.avalon.meta.info.InfoDescriptor;

import org.apache.excalibur.configuration.ConfigurationUtil;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.7 $ $Date: 2004/01/01 23:34:45 $
 */
public class XMLDeploymentProfileCreator extends XMLProfileCreator
{
   /**
    * Creation of a {@link DeploymentProfile} from an XML configuration.
    *
    * @param config the configuration instance describing the component deployment scenario 
    * @return the deployment profile
    */
    public DeploymentProfile createDeploymentProfile( Configuration config )
      throws Exception
    {
        String classname = config.getAttribute( "class", null );
        if( null == classname )
        {
            String c = ConfigurationUtil.list( config );
            String error = 
              "Missing 'class' attribute in component declaration:\n" + c;
            throw new ConfigurationException( error );
        }
        return createDeploymentProfile( null, classname, config );
    }

   /**
    * Creation of a {@link DeploymentProfile} from an XML configuration.
    *
    * @param base the default name
    * @param config the configuration describing the component deployment scenario 
    * @return the deployment profile
    */
    public DeploymentProfile createDeploymentProfile( 
      String base, String classname, Configuration config )
      throws Exception
    {
        final String name = getName( base, config, "untitled" );
        return createDeploymentProfile( classname, config, name );
    }

   /**
    * Creation of a {@link DeploymentProfile} from an XML configuration.
    *
    * @param classname the name of the class identifying the underlying component type
    * @param config the configuration describing the component deployment scenario 
    * @return the deployment profile
    */
    public DeploymentProfile createDeploymentProfile( 
      String classname, Configuration config, String name )
      throws Exception
    {
        final boolean activation = getActivationPolicy( config, true );
        final int collection = getCollectionPolicy( config );

        final CategoriesDirective categories = 
          getCategoriesDirective( config.getChild( "categories", false ), name );
        final ContextDirective context = 
          getContextDirective( config.getChild( "context", false ) );
        final DependencyDirective[] dependencies = 
          getDependencyDirectives( config.getChild( "dependencies" ) );
        final StageDirective[] stages = 
          getStageDirectives( config.getChild( "stages" ) );
        final Parameters params = 
          getParameters( config.getChild( "parameters", false ) );
        final Configuration configuration = 
          config.getChild( "configuration", true );

        return new DeploymentProfile( 
          name, activation, collection, classname, categories, context, dependencies, 
          stages, params, configuration, Mode.EXPLICIT );
    }

   /**
    * Get the collection policy from a configuration.  If the collection
    * policy is not declared a null is returned indicating that the collection 
    * policy shall default to the component type collection policy. 
    *
    * @param config a configuration fragment holding a collection attribute
    * @return collection policy 
    */
    protected int getCollectionPolicy( Configuration config )
    {
        return InfoDescriptor.getCollectionPolicy( config.getAttribute( "collection", null ) );
    }

    protected DependencyDirective[] getDependencyDirectives( Configuration config )
      throws ConfigurationException
    {
        if( config != null )
        {
            ArrayList list = new ArrayList();
            Configuration[] deps = config.getChildren( "dependency" );
            for( int i=0; i<deps.length; i++ )
            {
                list.add( getDependencyDirective( deps[i] ) );
            }
            return (DependencyDirective[]) list.toArray( new DependencyDirective[0] );
        }
        return new DependencyDirective[0];
    }

    protected DependencyDirective getDependencyDirective( Configuration config )
      throws ConfigurationException
    {
        final String key = config.getAttribute( "key" );
        final String source = config.getAttribute( "source", null );
        if( source != null )
        {
            return new DependencyDirective( key, source );
        }
        else
        {
            Configuration[] children = config.getChildren( "select" );
            ArrayList list = new ArrayList();
            for( int i=0; i<children.length; i++ )
            {
                list.add( getSelectionDirective( children[i] ) );
            }
            SelectionDirective[] features = 
              (SelectionDirective[]) list.toArray( new SelectionDirective[0] );
            return new DependencyDirective( key, features );
        }
    }

    protected StageDirective[] getStageDirectives( Configuration config )
      throws ConfigurationException
    {
        if( config != null )
        {
            ArrayList list = new ArrayList();
            Configuration[] deps = config.getChildren( "stage" );
            for( int i=0; i<deps.length; i++ )
            {
                list.add( getStageDirective( deps[i] ) );
            }
            return (StageDirective[]) list.toArray( new StageDirective[0] );
        }
        return new StageDirective[0];
    }

    protected StageDirective getStageDirective( Configuration config )
      throws ConfigurationException
    {
        final String key = config.getAttribute( "key" );
        final String source = config.getAttribute( "source", null );
        if( source != null )
        {
            return new StageDirective( key, source );
        }
        else
        {
            Configuration[] children = config.getChildren( "select" );
            ArrayList list = new ArrayList();
            for( int i=0; i<children.length; i++ )
            {
                list.add( getSelectionDirective( children[i] ) );
            }
            SelectionDirective[] features = 
              (SelectionDirective[]) list.toArray( new SelectionDirective[0] );
            return new StageDirective( key, features );
        }
    }

    protected SelectionDirective getSelectionDirective( Configuration config )
      throws ConfigurationException
    {
        final String feature = config.getAttribute( "feature" );
        final String value = config.getAttribute( "value" );
        final String match = config.getAttribute( "match", "required" );
        final boolean optional = config.getAttributeAsBoolean( "optional", false );
        return new SelectionDirective( feature, value, match, optional );
    }

    protected Parameters getParameters( Configuration config )
      throws ConfigurationException
    {
        if( config != null )
        {
            return Parameters.fromConfiguration( config );
        }
        return null;
    }

    /**
     * Utility method to create a new context directive.
     *
     * @param config the context directive configuration
     * @return the context directive
     * @throws ConfigurationException if an error occurs
     */
    public ContextDirective getContextDirective( Configuration config )
        throws ConfigurationException
    {
        if( config == null )
        {
            return new ContextDirective( null );
        }

        if( config.getChildren( "import" ).length > 0 )
        {
            final String error = 
              "The 'context' tag format has changed."
              + " Please check Merlin home for details "
              + "http://avalon.apache.org/merlin";
            throw new ConfigurationException( error );
        }

        final String classname = config.getAttribute( "class", null );
        final String source = config.getAttribute( "source", null );
        EntryDirective[] entries = getEntries( config.getChildren( "entry" ) );
        return new ContextDirective( classname, entries, source );
    }

    /**
     * Utility method to create a set of entry directives.
     *
     * @param configs the entry directive configurations
     * @return the entry directives
     * @throws ConfigurationException if an error occurs
     */
    protected EntryDirective[] getEntries( Configuration[] configs )
        throws ConfigurationException
    {
        ArrayList list = new ArrayList();
        for( int i = 0; i < configs.length; i++ )
        {
            Configuration conf = configs[ i ];
            final String key = conf.getAttribute( "key" );
            final Configuration[] children = conf.getChildren();
            if( children.length != 1 )
            {
                final String error = 
                  "Entry '" + key + "' does not contain one child element.";
                throw new ConfigurationException( error );
            }

            Configuration child = children[0];
            final String name = child.getName();
            if( name.equals( "import" ) )
            {
                final String importKey = child.getAttribute( "key" );
                list.add( new ImportDirective( key, importKey ) );
            }
            else if( name.equals( "constructor" ) )
            {
                final String classname = 
                  child.getAttribute( "class", "java.lang.String" );
                Configuration[] paramsConf = child.getChildren( "param" );
                if( paramsConf.length > 0 )
                {
                    Parameter[] params = getParameters( paramsConf );
                    ConstructorDirective constructor = 
                      new ConstructorDirective( key, classname, params );
                    list.add( constructor );
                }
                else
                {
                    ConstructorDirective constructor = 
                      new ConstructorDirective( 
                        key, classname, (String) child.getValue( null ) );
                    list.add( constructor );
                }
            }
            else
            {
                final String error = 
                  "Entry child unrecognized: " + name;
                throw new ConfigurationException( error );
            }
        }
        return (EntryDirective[])list.toArray( new EntryDirective[ 0 ] );
    }

    /**
     * Utility method to create a set of parameter directive.
     *
     * @param configs the parameter directive configurations
     * @return the parameter directives
     * @throws ConfigurationException if an error occurs
     */
    protected Parameter[] getParameters( Configuration[] configs )
        throws ConfigurationException
    {
        ArrayList list = new ArrayList();
        for( int i = 0; i < configs.length; i++ )
        {
            Parameter parameter = getParameter( configs[ i ] );
            list.add( parameter );
        }
        return (Parameter[])list.toArray( new Parameter[ 0 ] );
    }

    /**
     * Utility method to create a new parameter directive.
     *
     * @param config the parameter directive configuration
     * @return the parameter directive
     * @throws ConfigurationException if an error occurs
     */
    protected Parameter getParameter( Configuration config )
        throws ConfigurationException
    {
        String classname = config.getAttribute( "class", "java.lang.String" );
        Configuration[] params = config.getChildren( "param" );
        if( params.length == 0 )
        {
            String value = config.getValue( null );
            return new Parameter( classname, value );
        }
        else
        {
            Parameter[] parameters = getParameters( params );
            return new Parameter( classname, parameters );
        }
    }
}
