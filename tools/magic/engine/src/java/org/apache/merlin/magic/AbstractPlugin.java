package org.apache.merlin.magic;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;

public abstract class AbstractPlugin extends AbstractLogEnabled
    implements Plugin, Contextualizable
{
    private ArrayList m_Listeners;
    
    protected PluginContext m_Context;
    
    protected AbstractPlugin()
    {
        m_Listeners = new ArrayList();
    }
    
    public void contextualize( Context context )
    {
        m_Context = (PluginContext) context;
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

    protected void notifyStep( String method, int stepIndex )
    {
        Iterator list = m_Listeners.iterator();
        while( list.hasNext() )
        {
            PluginObserver observer = (PluginObserver) list.next();
            observer.postMethod( this, method, stepIndex );
        }
    }
} 
