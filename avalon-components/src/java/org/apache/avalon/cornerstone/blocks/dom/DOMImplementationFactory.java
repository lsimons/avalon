/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */

package org.apache.avalon.cornerstone.blocks.dom;

import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.phoenix.Block;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMException;


public class DOMImplementationFactory extends AbstractLoggable implements Block, Configurable, DOMImplementation {

    protected DOMImplementation  mDOMImplementation;

    public void dispose()
    {
        mDOMImplementation = null;
    }

    public void configure( Configuration configuration ) throws ConfigurationException {
        String domClass = configuration.getChild("domimplementation-class-name").getValue();
        try {
            mDOMImplementation = (DOMImplementation) Class.forName(domClass).newInstance();
        } catch (ClassNotFoundException cnfe) {
            throw new ConfigurationException("ClassNotFoundException for DOM implementaion factory",cnfe);
        } catch (InstantiationException ie) {
            throw new ConfigurationException("InstantiationException for DOM implementaion factory",ie);
        } catch (IllegalAccessException ie) {
            throw new ConfigurationException("IllegalAccessException for DOM implementaion factory",ie);
        }

    }

    // Methods from DOMImplementaion interface.  We're acting as a proxy here.

    public Document createDocument(String s, String s1, DocumentType type) throws DOMException {
        return mDOMImplementation.createDocument(s,s1,type);
    }

    public DocumentType createDocumentType(String s, String s1, String s2) throws DOMException {
        return mDOMImplementation.createDocumentType(s,s1,s2);
    }

    public boolean hasFeature(String s, String s1) {
        return mDOMImplementation.hasFeature(s,s1);
    }
}

