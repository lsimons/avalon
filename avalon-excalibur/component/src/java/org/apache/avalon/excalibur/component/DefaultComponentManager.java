/* 
 * Copyright 2002-2004 The Apache Software Foundation
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
package org.apache.avalon.excalibur.component;

/**
 * Default component manager for Avalon's components.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:14 $
 * @since 4.0
 * @deprecated  Please use <code>ExcaliburComponentManager</code> instead
 */
public class DefaultComponentManager
    extends ExcaliburComponentManager
{
    public DefaultComponentManager()
    {
        super();
    }

    /** Create the ComponentLocator with a Classloader */
    public DefaultComponentManager( final ClassLoader loader )
    {
        super( loader );
    }
}
