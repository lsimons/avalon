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

package org.apache.avalon.meta.info.test;

import org.apache.avalon.meta.info.CategoryDescriptor;
import org.apache.avalon.meta.info.Descriptor;

/**
 * CategoryTestCase does XYZ
 *
 * @author <a href="mcconnell.at.apache.org">Stephen McConnell</a>
 * @version CVS $ Revision: 1.1 $
 */
public class CategoryDescriptorTestCase extends AbstractDescriptorTestCase
{
    private final String m_name = "name";

    public CategoryDescriptorTestCase( String name )
    {
        super( name );
    }

    protected Descriptor getDescriptor()
    {
        return new CategoryDescriptor(m_name, getProperties());
    }


    protected void checkDescriptor( Descriptor desc )
    {
        super.checkDescriptor( desc );
        CategoryDescriptor cat = (CategoryDescriptor) desc;

        assertEquals( m_name, cat.getName() );
    }
}
