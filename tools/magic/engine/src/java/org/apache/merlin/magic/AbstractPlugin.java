package org.apache.merlin.magic;

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
