/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

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
package org.apache.avalon.fortress.impl;

import org.apache.avalon.framework.configuration.Configuration;

/**
 * A class holding metadata about a component handler.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2003/03/05 15:02:33 $
 */
public class ComponentHandlerMetaData
{
    private final String m_name;
    private final String m_classname;
    private final Configuration m_configuration;
    private final boolean m_lazyActivation;

   /**
    * Creation of a new impl handler meta data instance.
    * @param name the handler name
    * @param classname the handler classname
    * @param configuration the handler configuration
    * @param laxyActivation the activation policy
    */
    public ComponentHandlerMetaData( final String name,
                                     final String classname,
                                     final Configuration configuration,
                                     final boolean lazyActivation )
    {
        if( null == name )
        {
            throw new NullPointerException( "name" );
        }
        if( null == classname )
        {
            throw new NullPointerException( "classname" );
        }
        if( null == configuration )
        {
            throw new NullPointerException( "configuration" );
        }

        m_name = name;
        m_classname = classname;
        m_configuration = configuration;
        m_lazyActivation = lazyActivation;
    }

   /**
    * Returns the handler name
    * @return the handler name
    */
    public String getName()
    {
        return m_name;
    }

   /**
    * Returns the handler classname
    * @return the classname
    */
    public String getClassname()
    {
        return m_classname;
    }

   /**
    * Returns the handler configuration
    * @return the configuration
    */
    public Configuration getConfiguration()
    {
        return m_configuration;
    }

   /**
    * Returns the handler activation policy
    * @return the activation policy
    */
    public boolean isLazyActivation()
    {
        return m_lazyActivation;
    }
}
