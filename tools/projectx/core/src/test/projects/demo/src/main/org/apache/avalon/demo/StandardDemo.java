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

package org.apache.avalon.demo;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import org.apache.avalon.widget.Widget;
import org.apache.avalon.widget.StandardWidget;
import org.apache.avalon.gizmo.Gizmo;
import org.apache.avalon.gizmo.StandardGizmo;


/**
 * Sample demo implementation. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class StandardDemo extends Task implements Demo 
{

    public void init()
    {
        Widget widget = new StandardWidget();
        Gizmo gizmo = new StandardGizmo();
    }

    public void execute()
    {
        log( "hello world!" );
    }
}
