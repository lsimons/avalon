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

package org.apache.avalon.composition.data.builder;

import java.util.ArrayList;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.avalon.logging.data.CategoriesDirective;
import org.apache.avalon.logging.data.CategoryDirective;

import org.apache.avalon.composition.data.DeploymentProfile;

import org.apache.avalon.util.i18n.ResourceManager;
import org.apache.avalon.util.i18n.Resources;

import org.apache.excalibur.configuration.ConfigurationUtil;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.5 $ $Date: 2004/03/10 10:52:18 $
 */
public abstract class XMLProfileCreator
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( XMLProfileCreator.class );

   /**
    * Get the profile name.
    *
    * @param config a configuration fragment describing the profile.
    */
    protected String getName( 
      final String base, final Configuration config, final String defaultName )
    {
        final String name = config.getAttribute( "name", defaultName );
        if( base == null )
        {
            return name;
        }
        else
        {
            return base + "-" + name; 
        }
    }

   /**
    * Get the activation policy from a configuration. If no activation attribute
    * is present the value return defaults to FALSE (i.e. activation is deferred).
    *
    * @param config a configuration fragment holding a activation attribute
    * @return TRUE is the value of the activation attribute is 'true' or 'startup'
    *   otherwise the return value is FALSE
    */
    protected int getActivationDirective( Configuration config )
    {
        return getActivationDirective( config, DeploymentProfile.DEFAULT );
    }

   /**
    * Get the activation policy from a configuration. 
    *
    * @param config a configuration fragment holding a activation attribute
    * @param fallback the default policy
    * @return activation policy
    */
    protected int getActivationDirective( Configuration config, int fallback )
    {
        final String value = config.getAttribute( "activation", null );
        if( value == null )
        {
            return fallback;
        }

        final String string = value.toLowerCase().trim();
        if( string.equals( "startup" ) || string.equals( "true" ) )
        {
            return DeploymentProfile.ENABLED;
        }
        else if( string.equals( "lazy" ) || string.equals( "false" ) )
        {
            return DeploymentProfile.DISABLED;
        }
        else
        {
            return DeploymentProfile.DEFAULT;
        }
    }

    public CategoriesDirective getCategoriesDirective( 
      Configuration config, String name )
      throws ConfigurationException
    {
        if( config != null )
        {
            String priority = config.getAttribute( "priority", null );
            String target = target = config.getAttribute( "target", null );
            CategoryDirective[] categories = 
              getCategoryDirectives( config.getChildren( "category" ) );
            return new CategoriesDirective( name, priority, target, categories );
        }
        return null;
    }

    private CategoryDirective[] getCategoryDirectives( Configuration[] children )
      throws ConfigurationException
    {
        ArrayList list = new ArrayList();
        for( int i = 0; i < children.length; i++ )
        {
            CategoryDirective category = getCategoryDirective( children[ i ] );
            list.add( category );
        }
        return (CategoryDirective[]) list.toArray( new CategoryDirective[0] );
    }

    public CategoryDirective getCategoryDirective( Configuration config )
      throws ConfigurationException
    {
        try
        {
            final String name = config.getAttribute( "name" );
            final String priority = config.getAttribute( "priority", null );
            final String target = config.getAttribute( "target", null );
            return new CategoryDirective( name, priority, target );
        }
        catch( ConfigurationException e )
        {
            final String error = 
              "Invalid category descriptor."
              + ConfigurationUtil.list( config );
            throw new ConfigurationException( error, e );
        }
    }

}
