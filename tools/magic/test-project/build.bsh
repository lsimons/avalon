/*
Copyright 2004 The Apache Software Foundation
Licensed  under the  Apache License,  Version 2.0  (the "License");
you may not use  this file  except in  compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed  under the  License is distributed on an "AS IS" BASIS,
WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
implied.

See the License for the specific language governing permissions and
limitations under the License.
*/

import org.apache.avalon.magic.Plugin;
import org.apache.avalon.magic.PluginContext;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;

import org.apache.avalon.framework.logger.AbstractLogEnabled;

import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;

import org.apache.tools.ant.Project;

public class TestProjectPlugin extends AbstractLogEnabled
    implements Contextualizable, Serviceable, Plugin
{
    /* JavacPlugin type is not loaded yet */
    private Object m_JavacPlugin;
    
    private PluginContext m_Context;
    private Project m_Project;
    
    public void contextualize( Context context )
    {
        m_Context = (PluginContext) context;
        m_Project = context.getAntProject();
    }

    public void service( ServiceManager man )
    {
        m_JavacPlugin = man.lookup( "java" );
    }

    public void initialize()
    {
        JavacPlugin javac = (JavacPlugin) m_JavacPlugin;
        javac.addCompileListener( this );
    }
} 
