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

package org.apache.avalon.meta.info.builder.tags;


import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A doclet tag handler supporting 'logger' tags.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/02/21 13:27:04 $
 */
public class SchemaTag extends AbstractTag
{
    /**
     * The default logger class.
     */
    protected static final String CONFIGURATION_CLASS =
            "org.apache.avalon.framework.configuration.Configuration";

    /**
     * The javadoc key for the logger tag.
     */
    protected static final String KEY = "configuration";

    /**
     * The javadoc parameter name for the logging channel name
     */
    public static final String SCHEMA_PARAM = "schema";

    private JavaMethod[] m_methods;

    /**
     * The configuration schema tag constructor.
     * @param clazz the javadoc class descriptor.
     */
    public SchemaTag( final JavaClass clazz )
    {
        super( clazz );
        setMethods();
    }

    /**
     * Return a schema descriptor string if present
     * @return the set of logger descriptos
     */
    public String getConfigurationSchema()
    {
        if( m_methods.length > 0 )
        {
            final DocletTag[] tags =
             m_methods[0].getTagsByName( getNS() + Tags.DELIMITER + KEY );
            if( tags.length > 0 )
            {
                DocletTag tag = tags[0];
                return getNamedParameter( tag, SCHEMA_PARAM, null );
            }
        }
        return null;
    }

    /**
     * Set the value of the composition method.
     */
    private void setMethods()
    {
        m_methods = getLifecycleMethods( "configure", CONFIGURATION_CLASS );
    }
}
