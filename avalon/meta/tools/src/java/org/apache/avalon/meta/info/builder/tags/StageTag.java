/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 2002-2003 The Apache Software Foundation. All rights reserved.

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

package org.apache.avalon.meta.info.builder.tags;

import java.util.ArrayList;

import org.apache.avalon.meta.info.StageDescriptor;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * A doclet tag handler for the 'stage' tag.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 08:16:17 $
 */
public class StageTag extends AbstractTag
{
   /**
    * The stage tag key.
    */
    public static final String KEY = "stage";

   /**
    * The stage tag type parameter name (deprecated see id).
    */
    public static final String TYPE_PARAM = "type";

   /**
    * The stage tag id parameter name.
    */
    public static final String LEGACY_URN_PARAM = "urn";

   /**
    * The stage tag id parameter name.
    */
    public static final String ID_PARAM = "id";

   /**
    * Stage tag constructor.
    * @param clazz the javadoc class descriptor
    */
    public StageTag( final JavaClass clazz )
    {
        super( clazz );
    }

   /**
    * Return an array of StageDescriptor instances based on declared 'stage' tags.
    * @return the stage descriptors
    * @exception IllegalArgumentException if the tag is declared but does not 
    *   contain a value
    */
    public StageDescriptor[] getStages()
    {
        final ArrayList list = new ArrayList();
        final DocletTag[] tags = 
          getJavaClass().getTagsByName( getNS() + Tags.DELIMITER + KEY );
        for( int i = 0; i < tags.length; i++ )
        {
            list.add( getStage( tags[i] ) );
        }
        return (StageDescriptor[])list.toArray( new StageDescriptor[ list.size() ] );
    }

    private StageDescriptor getStage( DocletTag tag )
    {
        String value = getNamedParameter( tag, TYPE_PARAM, null );
        if( value != null )
        {
            final String type = resolveType( value );
            return new StageDescriptor( type );
        }
        else
        {
            value = getNamedParameter( tag, LEGACY_URN_PARAM, null );
            if( value == null )
            {
                value = getNamedParameter( tag, ID_PARAM );
            }
            return new StageDescriptor( value );
        }
    }
}
