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

package org.apache.avalon.composition.info;

/**
 * Descriptor used to mark a lifecycle artifact handler a
 * using a injection by constructor delivery strategy.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/03/13 23:26:58 $
 */
public class StagedDeliveryDescriptor extends DeliveryDescriptor
{
    private final Class m_delivery;

    public StagedDeliveryDescriptor( Class casting, Class delivery )
    {
        super( casting );
        m_delivery = delivery;
    }

    public Class getDeliveryInterfaceClass()
    {
        return m_delivery;
    }
}
