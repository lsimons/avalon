/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.demos.xcommander;

import java.util.ArrayList;
import org.apache.avalon.cornerstone.demos.xcommander.saxhandlers.EmptyElementHandler;
import org.xml.sax.Attributes;

/**
 * Defines the interface to be implemented by a class that is capable of receiving
 * events fired by the DefaultSAXHandler.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public interface ElementHandler
{
    /** This method is called by the eh its parent after the eh is constructed
     *  and validated to be of the right type, but before addChild() is called.
     */
    void start( Attributes attributes );

    /** This method is called by the eh its parent after all child eh have been
     *  added, started and stopped. */
    void end( String contents );

    /** This method is called by the eh its parent when a child element is found.
     */
    void addChild( ElementHandler elementHandler ) 
        throws IllegalArgumentException;

    /** This method is called by the eh its parent when addChild() throws its
     *  exception, providing a special eh - an EmptyElementHandler.
     */
    void addEmptyChild( EmptyElementHandler emptyElementHandler );

    /** This method is probably redundant... */
    boolean removeChild( ElementHandler elementHandler );

    ArrayList getChildren();

    /** This method checks whether two elements are of an equal type. It
     *  checks for similar namespaceURIs and localNames, and also
     *  if the type attributes (when available) are equal.
     */
    boolean equalTypes( ElementHandler elementHandler );

    /* calls equalTypes */
    boolean equals( Object object );

    /** @return At the moment, this does not return the namespace URI, but rather the name of the element handled.*/
    String getNamespaceURI();

    /** @return At the moment, this does not return the localname, but rather the name of the element handled.*/
    String getLocalName();
}
