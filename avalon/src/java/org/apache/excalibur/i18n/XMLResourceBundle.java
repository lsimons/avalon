/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.i18n;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import org.apache.xalan.xpath.XObject;
import org.apache.xalan.xpath.XPath;
import org.apache.xalan.xpath.XPathProcessorImpl;
import org.apache.xalan.xpath.XPathSupport;
import org.apache.xalan.xpath.xml.PrefixResolverDefault;
import org.apache.xalan.xpath.xml.XMLParserLiaisonDefault;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.TextImpl;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:mengelhart@earthtrip.com">Mike Engelhart</a>
 * @author <a href="mailto:neeme@one.lv">Neeme Praks</a>
 * @author <a href="mailto:oleg@one.lv">Oleg Podolsky</a>
 * @version $Id: XMLResourceBundle.java,v 1.1 2001/04/17 03:07:47 donaldp Exp $
 */
public class XMLResourceBundle {
    // Cache for storing string values for existing XPaths
    private Hashtable cacheIS = new Hashtable();
    // Cache for storing non-existing XPaths
    private Hashtable cacheNO = new Hashtable();

    private Document resource;
    public String bundleName = ""; //used by getLocale()
    protected XMLResourceBundle parent = null;


    public XMLResourceBundle( Document doc, String name, XMLResourceBundle p ) {
        System.out.print( "Constructing XMLResourceBundle: " + name );
        if ( p != null )
            System.out.println( "  --> parent: " + p.bundleName );
        else
            System.out.println( "  --> parent: " + p );

        this.resource = doc;
        this.bundleName = name;
        this.parent = p;
    }

    public void addToCache( String key, String value ) {
        cacheIS.put( key, value );
    }

    public Document getResource() {
        return this.resource;
    }

    // gets string without throwing an exception, returns empty string instead
    public String getStringSimple( String xPathKey ) {
        String result = "";
        try {
            result = getString( xPathKey );
        } catch ( MissingResourceException e ) {
            // do nothing
        }



        return result;
    }

    public String getString( String xPathKey ) throws MissingResourceException {
        if ( cacheIS.containsKey( xPathKey ) )
            return ( String ) cacheIS.get( xPathKey );
        if ( cacheNO.containsKey( xPathKey ) )
            new MissingResourceException( "Unable to locate resource: " + xPathKey, "XMLResourceBundle", xPathKey );

        Node root = this.resource.getDocumentElement();
        try {
            Node node = XPathAPI.selectSingleNode( root, xPathKey );
            if ( node != null ) {
                String temp = getTextNodeAsString( node );
                addToCache( xPathKey, temp );
                return temp;
            } else {
                if ( this.parent != null )
                    return this.parent.getString( xPathKey );
                else
                    throw new Exception();
            }
        } catch ( Exception e ) {
            // no nodes returned??
            cacheNO.put( xPathKey, "" );
            throw new MissingResourceException( "Unable to locate resource: " + xPathKey, "XMLResourceBundle", xPathKey );
        }
    }

    public String getString( Node role, String key ) throws MissingResourceException {
        try {
            Node node = XPathAPI.selectSingleNode( role, key );
            if ( node != null )
                return getTextNodeAsString( node );
            else
                throw new Exception();
        } catch ( Exception e ) {
            // no nodes returned??
            throw new MissingResourceException( "Unable to locate resource: " + key, "XMLResourceBundle", key );
        }
    }

    private String getTextNodeAsString( Node node ) throws MissingResourceException {
        node = node.getFirstChild();
        if ( node.getNodeType() == Node.TEXT_NODE )
            return ( ( TextImpl ) node ).getData();
        else
            throw new MissingResourceException( "Unable to locate XMLResourceBundle", "XMLResourceBundleFactory", "" );
    }

    public Node getRole( String xPath ) {
        Node root = resource.getDocumentElement();
        try {
            Node node = XPathAPI.selectSingleNode( root, xPath );
            if ( node != null )
                return node;
            else
                throw new Exception();
        } catch ( Exception e ) {
            // no nodes returned??
            throw new MissingResourceException( "Unable to locate resource: " + xPath, "XMLResourceBundle", xPath );
        }
    }

    public Node getRole( Node role, String xPath ) {
        try {
            Node node = XPathAPI.selectSingleNode( role, xPath );
            if ( node != null )
                return node;
            else
                throw new Exception();
        } catch ( Exception e ) {
            // no nodes returned??
            throw new MissingResourceException( "Unable to locate resource: " + xPath, "XMLResourceBundle", xPath );
        }
    }

    public XPath createXPath( String str, Node namespaceNode ) throws SAXException {
        XPathSupport xpathSupport = new XMLParserLiaisonDefault();

        if ( null == namespaceNode )
            throw new SAXException( "A namespace node is required to resolve prefixes!" );

        PrefixResolverDefault prefixResolver = new PrefixResolverDefault( ( namespaceNode.getNodeType() == Node.DOCUMENT_NODE ) ? ( ( Document ) namespaceNode ).getDocumentElement() : namespaceNode );

        // Create the XPath object.
        XPath xpath = new XPath();

        // Create a XPath parser.
        XPathProcessorImpl parser = new XPathProcessorImpl( xpathSupport );
        parser.initXPath( xpath, str, prefixResolver );

        return xpath;
    }

    public Locale getLocale() {
        String bundle = bundleName.substring( 0, bundleName.indexOf( ".xml" ) );
        int localeStart = bundle.indexOf( "_" );
        if ( localeStart == -1 )
            return new Locale( "", "", "" );
        bundle = bundle.substring( localeStart + 1 );
        localeStart = bundle.indexOf( "_" );
        if ( localeStart == -1 )
            return new Locale( bundle, "", "" );

        String lang = bundle.substring( 0, localeStart );
        bundle = bundle.substring( localeStart + 1 );
        localeStart = bundle.indexOf( "_" );
        if ( localeStart == -1 )
            return new Locale( lang, bundle, "" );

        String country = bundle.substring( 0, localeStart );
        bundle = bundle.substring( localeStart + 1 );
        return new Locale( lang, country, bundle );
    }
}

