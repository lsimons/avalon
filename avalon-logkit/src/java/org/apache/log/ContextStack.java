/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log;

import java.util.Stack;

/**
 * The ContextStack records the nested context of an application.
 * The context is an application defined characteristeric. For instance
 * a webserver context may be defined as the session that is currently
 * used to connect to server. A application may have context 
 * defined by current thread. A applet may have it's context defined 
 * by the name of the applet etc.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ContextStack 
{
    ///Thread local for holding instance of stack associated with current thread
    private static final ThreadLocal c_context   = new ThreadLocal();

    ///Container to hold stack of elements
    private Stack                    m_stack     = new Stack();

    /**
     * Get the Current ContextStack.
     * This returns a ContextStack associated with current thread. If the
     * thread doesn't have a ContextStack associated with it then a new
     * ContextStack is created with the name of thread as base context.
     *
     * @return the current ContextStack
     */
    public final static ContextStack getCurrentContext()
    {
        ContextStack context = (ContextStack)c_context.get();

        if( null == context )
        {
            context = new ContextStack();
            context.push( Thread.currentThread().getName() );
            c_context.set( context );
        }

        return context;
    }

    /**
     * Empty the context stack.
     *
     */
    public void clear()
    {
        m_stack.setSize( 0 );
    }

    /**
     * Get the context at a particular depth.
     *
     * @param index the depth of the context to retrieve
     * @return the context
     */
    public Object get( final int index )
    {
        return m_stack.elementAt( index );
    }
  
    /**
     * Remove a context from top of stack and return it.
     *
     * @return the context that was on top of stack
     */
    public Object pop()
    {
        return m_stack.pop();
    }
  
    /**
     * Push the context onto top of context stack.
     *
     * @param context the context to place on stack
     */
    public void push( final Object context )
    {
        m_stack.push( context );
    }
  
    /**
     * Set the current ContextSet to be equl to other ContextStack.
     *
     * @param stack the value to copy
     */
    public void set( final ContextStack stack )
    {
        clear();
        final int size = stack.m_stack.size();
    
        for( int i = 0; i < size; i++ )
        {
            m_stack.push( stack.m_stack.elementAt( i ) );
        }
    }
  
    /**
     * Get the number of contexts in stack.
     *
     * @return the number of contexts in stack
     */
    public int getSize()
    {
        return m_stack.size();
    }

    /**
     * Format context stack into a string.
     * Each element in stack is printed out, separated by a '.' character.
     *
     * @return the string describing context stack
     */
    public String toString()
    {
        return toString( getSize() );
    }

    /**
     * Format context stack into a string.
     * Only write a maximum of count elements, separated by '.' separator.
     * Note that elements in stack will have toString() called and every occurence
     * of spearator character '.' replaced with '_'.
     *
     * @return the string describing context stack
     */
    public String toString( final int count )
    {
        final StringBuffer sb = new StringBuffer();

        final int end = getSize() - 1;
        final int start = Math.max( end - count + 1, 0 );

        for( int i = start; i < end; i++ )
        {
            sb.append( fix( get( i ).toString() ) );
            sb.append( '.' );
        }

        sb.append( fix( get( end ).toString() ) );

        return sb.toString();
    }

    /**
     * Correct a context string by replacing separators '.' with a '_'.
     *
     * @param context the un-fixed context
     * @return the fixed context
     */
    private String fix( final String context )
    {
        return context.replace( '.', '_' );
    }
}
