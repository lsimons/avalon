/*
 * Copyright 1997-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.facilities.console.commands;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.DeploymentModel;

import org.apache.avalon.facilities.console.Console;
import org.apache.avalon.facilities.console.ConsoleCommand;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;

import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;

/**
 * @avalon.component name="console-viewmodel" lifestyle="singleton"
 * @avalon.service type="org.apache.avalon.facilities.console.ConsoleCommand"
 */
public class ViewModelCmd
    implements ConsoleCommand, Serviceable, Contextualizable
{
    String LINE = 
      "\n-----------------------------------------------------------";
    
    private ContainmentModel m_RootModel;
    
    public String getName()
    {
        return "viewmodel";
    }
    
    public String getDescription()
    {
        String str = "usage: viewmodel (path)\n\nDisplays the composition model.";
        return str;
    }
    
    /**
     * Contextulaization of the listener by the container during 
     * which we are supplied with the root composition model for 
     * the application.
     *
     * @param ctx the supplied listener context
     *
     * @exception ContextException if a contextualization error occurs
     *
     * @avalon.entry key="urn:composition:containment.model" 
     *               type="org.apache.avalon.composition.model.ContainmentModel" 
     *
     */
    public void contextualize( Context ctx ) 
        throws ContextException
    {
        m_RootModel = (ContainmentModel) ctx.get( "urn:composition:containment.model" );
    }
    
    /**
     * @avalon.dependency type="org.apache.avalon.facilities.console.Console"
     *                    key="console"
     */
    public void service( ServiceManager man )
        throws ServiceException
    {
        Console console = (Console) man.lookup( "console" );
        console.addCommand( this );
    }
    
    public void execute( BufferedReader input, BufferedWriter output, String[] arguments )
        throws Exception
    {
        output.newLine();
        String path;
        if( arguments.length == 0 )
            path = "/";
        else
            path = arguments[0];
        DeploymentModel model = m_RootModel.getModel( path );
        
        String result = printModel( model );
        output.write( result );
        output.newLine();
        output.flush();
    }

    public String printModel( DeploymentModel model )
    {
        StringBuffer buffer = new StringBuffer( "audit report" );
        buffer.append( LINE );
        buffer.append( "\nApplication Model" );
        buffer.append( LINE );
        buffer.append( "\n" );
        printModel( buffer, "  ", model );
        buffer.append( "\n" );
        buffer.append( LINE );
        return buffer.toString();
    }

    public void printModel( StringBuffer buffer, String lead, DeploymentModel model )
    {
        if( model instanceof ContainmentModel )
        {
            printContainmentModel( buffer, lead, (ContainmentModel) model );
        }
        else if( model instanceof ComponentModel ) 
        {
            printComponentModel( buffer, lead, (ComponentModel) model );
        }
    }

    public void printContainmentModel( 
      StringBuffer buffer, String lead, ContainmentModel model )
    {
        buffer.append( 
          "\n" + lead 
          + "container:" 
          + model 
          + ")" );
        printDeploymentModel( buffer, lead, model );
        DeploymentModel[] models = model.getModels();
        if( models.length > 0 )
        {
            buffer.append( "\n" + lead + "  children:" );
            for( int i=0; i<models.length; i++ )
            {
                DeploymentModel m = models[i];
                printModel( buffer, "    " + lead, m );
            }
        }
        models = model.getStartupGraph();
        if( models.length > 0 )
        {
            buffer.append( "\n" + lead + "  startup:" );
            for( int i=0; i<models.length; i++ )
            {
                DeploymentModel m = models[i];
                buffer.append( "\n" + "    " + lead + (i+1) + ": " + m );
            }
        }
        models = ((ContainmentModel)model).getShutdownGraph();
        if( models.length > 0 )
        {
            buffer.append( "\n" + lead + "  shutdown:" );
            for( int i=0; i<models.length; i++ )
            {
                DeploymentModel m = models[i];
                buffer.append( "\n" + "    " + lead + (i+1) + ": " + m );
            }
        }
    }

    public void printComponentModel( 
      StringBuffer buffer, String lead, ComponentModel model )
    {
        buffer.append( 
          "\n" + lead 
          + "component:" 
          + model + "(" 
          + model.getDeploymentTimeout() 
          + ")" );
        printDeploymentModel( buffer, lead, model );
    }

    public void printDeploymentModel( 
      StringBuffer buffer, String lead, DeploymentModel model )
    {
        DeploymentModel[] providers = model.getProviders();
        DeploymentModel[] consumers = model.getConsumerGraph();

        if(( providers.length == 0 ) && ( consumers.length == 0 ))
        {
            return;
        }

        if( providers.length > 0 ) for( int i=0; i<providers.length; i++ )
        {
            DeploymentModel m = providers[i];
            buffer.append( "\n" + lead + "  <-- consumes: " + m  );
        }

        if( consumers.length > 0 ) for( int i=0; i<consumers.length; i++ )
        {
            DeploymentModel m = consumers[i];
            if( isDirectProvider( m, model ) )
            {
                buffer.append( "\n" + lead + "  --> supplies: " + m );
            }
        }
    }

    private boolean isDirectProvider( DeploymentModel consumer, DeploymentModel model )
    {
        String name = model.getQualifiedName();
        DeploymentModel[] providers = consumer.getProviders();
        for( int i=0; i<providers.length; i++ )
        {
             DeploymentModel m = providers[i];
             if( m.getQualifiedName().equals( name ) ) return true;
        }
        return false;
    }
}
 
