/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.dom;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;

/**
 * This service provides a way instantiate a DocumentBuilderFactory in a non static way.
 * It is in fact a clone of the abstract class of the same name in the javax.xml.parsers.
 * package.  With this slieght of hand we could have multiple parsers from multiple
 * classloaders active in one virtual machine.  Just the way Phoenix likes it.
 *
 * This in essence is a proxy.
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a>
 */
public interface DocumentBuilderFactory
{
    String ROLE = DocumentBuilderFactory.class.getName();

    /**
     * Creates a new instance of a {@link javax.xml.parsers.DocumentBuilder}
     * using the currently configured parameters.
     *
     * @exception ParserConfigurationException if a DocumentBuilder
     * cannot be created which satisfies the configuration requested.
     * @return A new instance of a DocumentBuilder.
     */
    DocumentBuilder newDocumentBuilder() 
        throws ParserConfigurationException;

    /**
     * Specifies that the parser produced by this code will
     * provide support for XML namespaces. By default the value of this is set
     * to <code>false</code>
     *
     * @param awareness true if the parser produced will provide support
     *                  for XML namespaces; false otherwise.
     */
    void setNamespaceAware( boolean awareness );

    /**
     * Specifies that the parser produced by this code will
     * validate documents as they are parsed. By default the value of this
     * is set to <code>false</code>.
     *
     * @param validating true if the parser produced will validate documents
     *                   as they are parsed; false otherwise.
     */
    void setValidating( boolean validating );

    /**
     * Specifies that the parsers created by this  factory must eliminate
     * whitespace in element content (sometimes known loosely as
     * 'ignorable whitespace') when parsing XML documents (see XML Rec
     * 2.10). Note that only whitespace which is directly contained within
     * element content that has an element only content model (see XML
     * Rec 3.2.1) will be eliminated. Due to reliance on the content model
     * this setting requires the parser to be in validating mode. By default
     * the value of this is set to <code>false</code>.
     *
     * @param whitespace true if the parser created must eliminate whitespace
     *                   in the element content when parsing XML documents;
     *                   false otherwise.
     */
    void setIgnoringElementContentWhitespace( boolean whitespace );

    /**
     * Specifies that the parser produced by this code will
     * expand entity reference nodes. By default the value of this is set to
     * <code>true</code>
     *
     * @param expandEntityRef true if the parser produced will expand entity
     *                        reference nodes; false otherwise.
     */
    void setExpandEntityReferences( boolean expandEntityRef );

    /**
     * Specifies that the parser produced by this code will
     * ignore comments. By default the value of this is set to <code>false
     * </code>
     */
    void setIgnoringComments( boolean ignoreComments );

    /**
     * Specifies that the parser produced by this code will
     * convert CDATA nodes to Text nodes and append it to the
     * adjacent (if any) text node. By default the value of this is set to
     * <code>false</code>
     *
     * @param coalescing  true if the parser produced will convert CDATA nodes
     *                    to Text nodes and append it to the adjacent (if any)
     *                    text node; false otherwise.
     */
    void setCoalescing( boolean coalescing );

    /**
     * Indicates whether or not the factory is configured to produce
     * parsers which are namespace aware.
     *
     * @return  true if the factory is configured to produce parsers which
     *          are namespace aware; false otherwise.
     */
    boolean isNamespaceAware();

    /**
     * Indicates whether or not the factory is configured to produce
     * parsers which validate the XML content during parse.
     *
     * @return  true if the factory is configured to produce parsers
     *          which validate the XML content during parse; false otherwise.
     */
    boolean isValidating();

    /**
     * Indicates whether or not the factory is configured to produce
     * parsers which ignore ignorable whitespace in element content.
     *
     * @return  true if the factory is configured to produce parsers
     *          which ignore ignorable whitespace in element content;
     *          false otherwise.
     */
    boolean isIgnoringElementContentWhitespace();

    /**
     * Indicates whether or not the factory is configured to produce
     * parsers which expand entity reference nodes.
     *
     * @return  true if the factory is configured to produce parsers
     *          which expand entity reference nodes; false otherwise.
     */
    boolean isExpandEntityReferences();

    /**
     * Indicates whether or not the factory is configured to produce
     * parsers which ignores comments.
     *
     * @return  true if the factory is configured to produce parsers
     *          which ignores comments; false otherwise.
     */
    boolean isIgnoringComments();

    /**
     * Indicates whether or not the factory is configured to produce
     * parsers which converts CDATA nodes to Text nodes and appends it to
     * the adjacent (if any) Text node.
     *
     * @return  true if the factory is configured to produce parsers
     *          which converts CDATA nodes to Text nodes and appends it to
     *          the adjacent (if any) Text node; false otherwise.
     */
    boolean isCoalescing();

    /**
     * Allows the user to set specific attributes on the underlying
     * implementation.
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     * @exception IllegalArgumentException thrown if the underlying
     * implementation doesn't recognize the attribute.
     */
    void setAttribute( String name, Object value )
        throws IllegalArgumentException;

    /**
     * Allows the user to retrieve specific attributes on the underlying
     * implementation.
     * @param name The name of the attribute.
     * @return value The value of the attribute.
     * @exception IllegalArgumentException thrown if the underlying
     * implementation doesn't recognize the attribute.
     */
    Object getAttribute( String name ) 
        throws IllegalArgumentException;
}
