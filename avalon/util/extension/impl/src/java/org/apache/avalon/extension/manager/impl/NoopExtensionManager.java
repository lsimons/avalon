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

package org.apache.avalon.extension.manager.impl;

import org.apache.avalon.extension.Extension;
import org.apache.avalon.extension.manager.ExtensionManager;
import org.apache.avalon.extension.manager.OptionalPackage;

/**
 * A Noop ExtensionManager that can't provide any extensions.
 * This is for use in certain environments (ala Servlets) that
 * require apps to be be self-contained.
 *
 * @author Peter Donald
 * @version $Revision: 1.1 $ $Date: 2004/02/04 17:24:17 $
 */
public class NoopExtensionManager
    implements ExtensionManager
{
    /**
     * Return an empty array of {@link OptionalPackage}s.
     *
     * @param extension the extension looking for
     * @see ExtensionManager#getOptionalPackages
     */
    public OptionalPackage[] getOptionalPackages( final Extension extension )
    {
        return new OptionalPackage[ 0 ];
    }
}
