/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.avalon.composition.data.builder;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.composition.data.CategoriesDirective;
import org.apache.avalon.composition.data.TargetDirective;
import org.apache.avalon.composition.data.Targets;

/**
 * Handles internalization of an XML based description of a {@link Targets}
 * instance from a Configuration object.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2003/10/28 20:21:00 $
 */
public class XMLTargetsCreator extends XMLDeploymentProfileCreator 
{
   /**
    * Create a set of target directives from the confiugration.
    * @param config the targets configuration
    */
    public Targets createTargets( Configuration config )
      throws Exception
    {
        Configuration[] children = config.getChildren( "target" );
        TargetDirective[] targets = new TargetDirective[ children.length ];
        for( int i=0; i<children.length; i++ )
        {
            targets[i] = createTargetDirective( children[i] );
        }
        return new Targets( targets );
    }

    /**
     * Create a {@link TargetDirective} from a configuration
     *
     * @param config the configuration
     * @return the target directive
     * @exception Exception if a error occurs during profile creation
     */
    private TargetDirective createTargetDirective( Configuration config )
      throws Exception
    {
        String name = config.getAttribute( "name", null ); // legacy
        if( name == null )
        {
            name = config.getAttribute( "path" );
        }
        final Configuration conf = config.getChild( "configuration", false );
        CategoriesDirective categories = 
           getCategoriesDirective( config.getChild( "categories", false ), name );
        return new TargetDirective( name, conf, categories );
    }
}
