/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.i18n;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Vector;
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
 * @version $Id: XMLResourceBundleFactory.java,v 1.1 2001/04/17 03:07:47 donaldp Exp $
 */
public class XMLResourceBundleFactory {
    protected static Hashtable cache = new Hashtable();
    protected static String directory;

    protected XMLResourceBundleFactory() {}




    public static XMLResourceBundle getBundle( String name ) throws MissingResourceException {
        return getBundle( name, Locale.getDefault() );
    }

    public static XMLResourceBundle getBundle( String name, Locale loc ) throws MissingResourceException {
        return getBundle( name, loc, false );
    }

    public static XMLResourceBundle getBundle( String name, Locale loc, boolean cacheAtStartup ) throws MissingResourceException {
        XMLResourceBundle parent = null;
        String bundleName = getBundleName( name, loc );
        // first look in the cache - if there grab it
        XMLResourceBundle bundle = getCachedBundle( bundleName );
        if ( bundle != null )
            return bundle;

        // if bundle is not in cache try loading the bundle using the given name and locale bundleName
        Document doc = null;
        doc = loadResourceBundle( bundleName );
        if ( doc != null ) {
            if ( ! loc.getLanguage().equals( "" ) )
                parent = getParentBundle( name, loc, cacheAtStartup );
            bundle = new XMLResourceBundle( doc, bundleName, parent );
            if ( cacheAtStartup )
                storeTextElements( bundle, bundle.getResource(), "" );
            updateCache( bundleName, bundle );
            return bundle;
        }
        // if the locale's language is "" then we've already tried to load the default resource and it's not available
        while ( ! loc.getLanguage().equals( "" ) ) {
            // if the given bundle name is not found, then try loading using a shortened Locale
            loc = getParentLocale( loc );
            bundleName = getBundleName( name, loc );
            // first look in the cache - if there grab it and return
            bundle = getCachedBundle( bundleName );
            if ( bundle != null )
                return bundle;

            // try loading the bundle using the given name and locale bundleName
            doc = loadResourceBundle( bundleName );
            if ( doc != null ) {
                if ( ! loc.getLanguage().equals( "" ) )
                    parent = getParentBundle( name, loc, cacheAtStartup );
                bundle = new XMLResourceBundle( doc, bundleName, parent );
                if ( cacheAtStartup )
                    storeTextElements( bundle, bundle.getResource(), "" );
                updateCache( bundleName, bundle );
                return bundle;
            }
        }
        throw new MissingResourceException( "Unable to locate resource: " + bundleName, "XMLResourceBundleFactory", "" );
    }

    protected synchronized static XMLResourceBundle getParentBundle( String name, Locale loc ) {
        return getParentBundle( name, loc, false );
    }

    protected synchronized static XMLResourceBundle getParentBundle( String name, Locale loc, boolean cacheAtStartup ) {
        loc = getParentLocale( loc );
        String bundleName = getBundleName( name, loc );
        Document doc = loadResourceBundle( bundleName );
        XMLResourceBundle bundle = null;
        if ( doc != null ) {
            if ( ! loc.getLanguage().equals( "" ) )
                bundle = getParentBundle( name, loc );
            bundle = new XMLResourceBundle( doc, bundleName, bundle );
            if ( cacheAtStartup )
                storeTextElements( bundle, bundle.getResource(), "" );
            updateCache( bundleName, bundle );
        }
        return bundle;
    }

    // this method returns the next locale up the parent hierarchy
    // e.g.; the parent of new Locale("en","us","mac")
    // would be new Locale("en", "us", "");
    protected static Locale getParentLocale( Locale loc ) {
        if ( loc.getVariant().equals( "" ) ) {
            if ( loc.getCountry().equals( "" ) )
                loc = new Locale( "", "", "" );
            else
                loc = new Locale( loc.getLanguage(), "", "" );
        } else
            loc = new Locale( loc.getLanguage(), loc.getCountry(), "" );

        return loc;
    }

    protected synchronized static XMLResourceBundle getCachedBundle( String bundleName ) {
        /*
          SoftReference ref = (SoftReference)(cache.get(bundleName));
          if (ref != null)
          return (XMLResourceBundle) ref.get();
          else
          return null;
        */
        return ( XMLResourceBundle ) ( cache.get( bundleName ) );
    }

    protected synchronized static void updateCache( String bundleName, XMLResourceBundle bundle ) {
        cache.put( bundleName, bundle );
    }

    /*        protected static String getBundleName(String name, Locale loc)
              {
              StringBuffer sb = new StringBuffer(name);
              if (! loc.getLanguage().equals(""))
              {
              sb.append("_");
              sb.append(loc.getLanguage());
              }
              if (! loc.getCountry().equals(""))
              {
              sb.append("_");
              sb.append(loc.getCountry());
              }
              if (! loc.getVariant().equals(""))
              {
              sb.append("_");
              sb.append(loc.getVariant());
              }
              // should all the files have an extension of .xml?  Seems reasonable
              sb.append(".xml");

              return sb.toString();
              }
    */

    protected static String getBundleName( String name, Locale loc ) {
        String lang = loc.getLanguage();
        StringBuffer sb = new StringBuffer( getDirectory() );

        if ( lang.length() > 0 ) sb.append( "/" ).append( lang );
        sb.append( "/" ).append( name ).append( ".xml" );

        return sb.toString();
    }

    public static XMLResourceBundle getBundle( String fileName, String localeName ) throws MissingResourceException {
        return getBundle( fileName, new Locale( localeName, localeName ) );
    }

    public static XMLResourceBundle getBundleFromFilename( String bundleName ) throws MissingResourceException {
        return getBundleFromFilename( bundleName, true );
    }

    public static XMLResourceBundle getBundleFromFilename( String bundleName, boolean cacheAtStartup ) throws MissingResourceException {
        Document doc = null;
        doc = loadResourceBundle( getDirectory() + "/" + bundleName );

        XMLResourceBundle bundle = getCachedBundle( bundleName );
        if ( bundle != null )
            return bundle;

        if ( doc != null ) {
            bundle = new XMLResourceBundle( doc, bundleName, null );
            if ( cacheAtStartup )
                storeTextElements( bundle, bundle.getResource(), "" );
            updateCache( bundleName, bundle );
            return bundle;
        }
        throw new MissingResourceException( "Unable to locate resource: " + bundleName, "XMLResourceBundleFactory", "" );
    }

    // Load the XML document based on bundleName
    protected static Document loadResourceBundle( String bundleName ) {
        try {
            DOMParser parser = new DOMParser();
            parser.parse( bundleName );
            return parser.getDocument();
        } catch ( IOException e ) {
            return null;
        }
        catch ( SAXException e ) {
            return null;
        }
    }

    public static void setDirectory( String dir ) {
        directory = dir;
    }

    public static String getDirectory() {
        return ( directory != null ? directory : "" );
    }

    // Steps through the bundle tree and stores all text element values
    // in bundle's cache, and also stores attributes for all element nodes.
    // Parent must be am element-type node.
    private static void storeTextElements( XMLResourceBundle bundle, Node parent, String pathToParent ) {
        NodeList children = parent.getChildNodes();
        int childnum = children.getLength();

        for ( int i = 0; i < childnum; i++ ) {
            Node child = children.item( i );

            if ( child.getNodeType() == Node.ELEMENT_NODE ) {
                String pathToChild = pathToParent + '/' + child.getNodeName();

                NamedNodeMap attrs = child.getAttributes();
                if ( attrs != null ) {
                    Node temp = null;
                    String pathToAttr = null;
                    int attrnum = attrs.getLength();
                    for ( int j = 0; j < attrnum; j++ ) {
                        temp = attrs.item( j );
                        pathToAttr = "/@" + temp.getNodeName();
                        bundle.addToCache( pathToChild + pathToAttr, temp.getNodeValue() );
                    }
                }

                String childValue = getTextValue( child );
                if ( childValue != null )
                    bundle.addToCache( pathToChild, childValue );
                else
                    storeTextElements( bundle, child, pathToChild );
            }
        }
    }

    private static String getTextValue( Node element ) {
        NodeList list = element.getChildNodes();
        int listsize = list.getLength();

        Node item = null;
        String itemValue = null;

        for ( int i = 0; i < listsize; i++ ) {
            item = list.item( i );
            if ( item.getNodeType() != Node.TEXT_NODE ) return null;
            itemValue = item.getNodeValue(); if ( itemValue == null ) return null;
            itemValue = itemValue.trim(); if ( itemValue.length() == 0 ) return null;
            return itemValue;
        }
        return null;
    }
}
