/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.services.soapification;

import java.util.NoSuchElementException;
import org.apache.avalon.phoenix.Service;

/**
 * This service provides a way to publist an arbitary object via SOAP.
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a>
 */
public interface SOAPification
{
    String ROLE = "org.apache.avalon.cornerstone.services.soapification.SOAPification";
  
    /**
     * Publish a set of interfaces
     *
     * @param obj the object to publish
     * @param publicationName The name to publish it as.
     * @param interfacesToExpose an array of interfaces that the synamic proxy should expose
     */
    void publish( Object obj, String publicationName, Class[] interfacesToExpose) 
        throws SOAPificationException;
    
    /**
     * Publish an interface 
     *
     * @param obj the object to publish
     * @param publicationName The name to publish it as.
     * @param interfaceToExpose an interface that the synamic proxy should expose
     */    
    void publish(Object obj, String publicationName, Class interfaceToExpose) 
        throws SOAPificationException;

    /**
     * Publish an object (no interfaces(s)).
     *
     * @param obj the object to publish
     * @param publicationName The name to publish it as.
     */    
    void publish(Object obj, String publicationName) 
        throws SOAPificationException;
    
    /**
     * Unpublish 
     *
     * @param publicationName the name of the object to unpublish 
     */    
    void unpublish(String publicationName) 
        throws SOAPificationException;
}
