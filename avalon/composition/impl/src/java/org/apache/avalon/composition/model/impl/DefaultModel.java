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

import org.apache.avalon.composition.model.Model;
import org.apache.avalon.composition.model.DependencyGraph;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.composition.data.Mode;


/**
 * Abstract model base class.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1.1.1.2.1 $ $Date: 2004/01/04 01:19:28 $
 */
public abstract class DefaultModel
  implements Model
{
    //==============================================================
    // static
    //==============================================================

    private static final Resources REZ =
      ResourceManager.getPackageResources( DefaultModel.class );

    //==============================================================
    // immutable state
    //==============================================================

    private final String m_name;

    private final String m_path;

    private final Logger m_logger;

    private final Mode m_mode;

    private final DependencyGraph m_graph;

    //==============================================================
    // constructor
    //==============================================================

   /**
    * Creation of an abstract model.  The model associated a 
    * name and a partition.
    *
    * @param path the profile partition
    * @param name the profile name
    */
    public DefaultModel( 
      final Logger logger, String path, final String name, Mode mode, DependencyGraph graph )
    {
        m_logger = logger;
        m_name = name;
        m_path = path;
        m_mode = mode;
        m_graph = graph;
    }

    //==============================================================
    // Model
    //==============================================================

   /**
    * Return the profile name.
    * @return the name
    */
    public String getName()
    {
        return m_name;
    }

   /**
    * Return the profile path.
    * @return the path
    */
    public String getPath()
    {
        return m_path;
    }

   /**
    * Return the model fully qualified name.
    * @return the fully qualified name
    */
    public String getQualifiedName()
    {
        return getPath() + getName();
    }

   /**
    * Return the mode of establishment.
    * @return the mode
    */
    public Mode getMode()
    {
        return m_mode;
    }

   /**
    * Return the set of models consuming this model.
    * @return the consumers
    */
    public Model[] getConsumerGraph()
    {
        return m_graph.getConsumerGraph( this );
    }

   /**
    * Return the set of models supplying this model.
    * @return the providers
    */
    public Model[] getProviderGraph()
    {
        return m_graph.getProviderGraph( this );
    }


    //==============================================================
    // implementation
    //==============================================================

   /**
    * Return the logging channel.
    * @return the logger
    */
    protected Logger getLogger()
    {
        return m_logger;
    }

    public String toString()
    {
        return "[model: " + getQualifiedName() + "]";
    }

}
