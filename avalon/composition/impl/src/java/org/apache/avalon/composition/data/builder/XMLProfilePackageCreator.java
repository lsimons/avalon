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
import org.apache.avalon.composition.data.MetaDataException;
import org.apache.avalon.composition.data.ComponentProfile;
import org.apache.avalon.composition.data.ProfilePackage;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.excalibur.configuration.ConfigurationUtil;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1.1.1.2.1 $ $Date: 2004/01/09 20:29:49 $
 */
public class XMLProfilePackageCreator
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( XMLProfilePackageCreator.class );

    private static final XMLComponentProfileCreator DEPLOYMENT_CREATOR = 
      new XMLComponentProfileCreator();

   /**
    * Creation of a {@link ProfilePackage} from an XML configuration.
    *
    * @param config the configuration
    * @return the profile package
    */
    public ProfilePackage createProfilePackage( 
      final String base, String classname, Configuration config )
      throws MetaDataException
    {
        ArrayList list = new ArrayList();
        Configuration[] children = config.getChildren();
        for( int i=0; i<children.length; i++ )
        {
            Configuration child = children[i];
            final String name = child.getName();
            if( name.equals( "profile" ) )
            {
                try
                {
                    list.add( 
                      DEPLOYMENT_CREATOR.createComponentProfile( 
                        base, classname, child ) );
                }
                catch( Throwable e )
                {
                    final String error = 
                      "Unable to create a packaged deployment profile."
                      + ConfigurationUtil.list( child );
                    throw new MetaDataException( error );
                }
            }
            else
            {
                final String error =
                  "Package defintion contains an unrecognized profile"
                  + ConfigurationUtil.list( child );
                throw new MetaDataException( error );
            }
        }

        return new ProfilePackage( 
          (ComponentProfile[]) list.toArray( new ComponentProfile[0] ) );
    }
}
