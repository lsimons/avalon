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

package org.apache.avalon.magic;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;

import org.apache.avalon.framework.logger.AbstractLogEnabled;

import org.apache.tools.ant.Project;

public abstract class AbstractPlugin extends AbstractLogEnabled
    implements Plugin, Contextualizable
{
    private ArrayList m_Listeners;
    
    protected PluginContext m_Context;
    protected Project m_Project;
    
    protected AbstractPlugin()
    {
        m_Listeners = new ArrayList();
    }
    
    public void contextualize( Context ctx )
    {
        m_Context = (PluginContext) ctx;
        m_Project = m_Context.getAntProject();
    }
    
    public void addPluginObserver( PluginObserver observer )
    {
        if( observer != null )
            m_Listeners.add( observer );
    }
    
    public void removePluginObserver( PluginObserver observer )
    {
        if( observer != null )
            m_Listeners.remove( observer );
    }
    
    protected void notifyPreMethod( String method )
    {
        Iterator list = m_Listeners.iterator();
        while( list.hasNext() )
        {
            PluginObserver observer = (PluginObserver) list.next();
            observer.preMethod( this, method );
        }
    }

    protected void notifyPostMethod( String method )
    {
        Iterator list = m_Listeners.iterator();
        while( list.hasNext() )
        {
            PluginObserver observer = (PluginObserver) list.next();
            observer.postMethod( this, method );
        }
    }

    protected void notifyStep( String method, String step )
    {
        Iterator list = m_Listeners.iterator();
        while( list.hasNext() )
        {
            PluginObserver observer = (PluginObserver) list.next();
            observer.stepPerformed( this, method, step );
        }
    }
} 
