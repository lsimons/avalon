/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.xcommander.xcommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.cornerstone.demos.xcommander.GlobalResult;
import org.apache.cornerstone.demos.xcommander.XCommand;

/**
 * This contains very basic Chat program functionality. All messages are sent
 * to all users.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class Chat 
    implements XCommand
{
    public GlobalResult addUser( String user )
    {
        return new AddUser( user );
    }

    public GlobalResult removeUser( String user )
    {
        return new RemoveUser( user );
    }

    public GlobalResult message( String msg )
    {
        return new Message( msg );
    }

    private class AddUser 
        implements GlobalResult
    {
        String m_name;

        AddUser( String name )
        {
            m_name = name;
        }

        public String toString()
        {
            return "<chat action=\"addUser\">"+m_name+"</chat>";
        }
    }

    private class RemoveUser 
        implements GlobalResult
    {
        String m_name;

        RemoveUser( String name )
        {
            m_name = name;
        }

        public String toString()
        {
            return "<chat action=\"removeUser\">"+m_name+"</chat>";
        }
    }

    private class Message 
        implements GlobalResult
    {
        String m_msg;

        Message( String msg )
        {
            m_msg = msg;
        }

        public String toString()
        {
            return "<chat action=\"message\">"+m_msg+"</chat>";
        }
    }
}
