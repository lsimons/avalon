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

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.composition.data.CategoriesDirective;
import org.apache.avalon.composition.data.CategoryDirective;
import org.apache.excalibur.configuration.ConfigurationUtil;


/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 09:31:42 $
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
    protected boolean getActivationPolicy( Configuration config )
    {
        return getActivationPolicy( config, false );
    }

   /**
    * Get the activation policy from a configuration. 
    *
    * @param config a configuration fragment holding a activation attribute
    * @param fallback the default policy
    * @return activation policy
    */
    protected boolean getActivationPolicy( Configuration config, boolean fallback )
    {
        final String value = config.getAttribute( "activation", null );
        if( value == null )
        {
            return fallback;
        }

        final String string = value.toLowerCase().trim();
        if( string.equals( "startup" ) || string.equals( "true" ) )
        {
            return true;
        }
        return fallback ;
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
