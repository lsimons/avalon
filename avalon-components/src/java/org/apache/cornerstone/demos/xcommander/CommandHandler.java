/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.xcommander;

import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Contains callback methods for handling requests and results of XCommands.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public interface CommandHandler 
{
    /** Provides the result returned from an XCommand. */
    void handleCommand( String type, String identifier, Object results );
    
    /** Asks for the class implementing the specified XCommand.
     *  You are encouraged to follow the java naming standard for
     *  XCommand names.
     *  @return the relevant Class, or null if the specified XCommand is not known. */
    Class getCommand( String commandName );
}
