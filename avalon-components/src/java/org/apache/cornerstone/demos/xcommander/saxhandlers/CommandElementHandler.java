/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.xcommander.saxhandlers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.apache.cornerstone.demos.xcommander.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This is the entrypoint into the ElementHandler web.
 *
 * This eh listens for &lt;command&gt; elements. It accepts
 * a single &lt;constructor&gt; and a single &lt;method&gt; as
 * children.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class CommandElementHandler 
    extends AbstractElementHandler 
{
    /** This is the method we try to call if none is specified */
    public final static String DEFAULT_METHODNAME = "toString";

    private String type;
    private String identifier;
    private ConstructorElementHandler ceh;
    private MethodElementHandler meh;

    public void addChild( final ElementHandler elementHandler ) 
        throws IllegalArgumentException
    {
        if( elementHandler instanceof ConstructorElementHandler )
        {
            ceh = (ConstructorElementHandler)elementHandler;
        }
        else if( elementHandler instanceof MethodElementHandler ) 
        {
            meh = (MethodElementHandler)elementHandler;
        } 
        else
        {
            throw new IllegalArgumentException();
        }
    }

    public void start( final Attributes attributes )
    {
        type = attributes.getValue( "","type" );
        identifier = attributes.getValue( "","identifier" );
    }

    /**
     * This method is called when xml parsing of the XCommand is complete.
     * It uses the supplied CommandHandler to get. After that,
     * some RMI is used to try and call the specified method.
     *
     * @param ch This receives the results of the XCommand.
     */
    public void executeCommand( final CommandHandler commandHandler ) 
        throws SAXException
    {
        // try to find the specified role
        // handlerClass = Class.forName( role );
        Class handlerClass = commandHandler.getCommand( type );

        if( handlerClass == null )
        {
            throw new SAXException( "the <command> " + identifier + 
                                    " could not be executed - unable to " + 
                                    "find the specified role: " + type + "!" );
        }

        Object o = null;
        Object result = null;

        if( null != ceh ) // otherwise, we assume a static method
        {
            try 
            {
                // try to get an object from the constructor specified
                // by ceh
                o = handlerClass.getConstructor( ceh.getArgumentTypes() ).
                    newInstance( ceh.getArguments() );

            } 
            catch( final Exception e )
            {
                // we'll try to use a static method
            }
        }

        if( null != meh ) // otherwise, we assume the default method
        {
            try
            {
                final Method m = handlerClass.getMethod( meh.getName(), meh.getArgumentTypes() );
                result = m.invoke( o, meh.getArguments() );
            } 
            catch( final Exception e )
            {
                throw new SAXException( "the <command> " + identifier + 
                                        " could not be executed - unable " + 
                                        "to find the specified method!", e );
            }
        } 
        else
        {
            Class[] arr = new Class[0];
            try
            {
                final Method m = handlerClass.getMethod( DEFAULT_METHODNAME, arr );
                result = m.invoke( o, null );
            } 
            catch( final Exception e )
            {
                throw new SAXException( "the <command> " + identifier + 
                                        " could not be executed - unable to " + 
                                        "find the default method!", e );
            }
        }
        
        commandHandler.handleCommand( type, identifier, result );
    }

    public String getNamespaceURI()
    {
        return "command";
    }

    public String getLocalName()
    {
        return "command";
    }
}
