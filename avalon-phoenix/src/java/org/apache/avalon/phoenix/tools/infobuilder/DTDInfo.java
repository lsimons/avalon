package org.apache.avalon.phoenix.tools.infobuilder;
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

/**
 * Holds information about a given DTD.
 */
public class DTDInfo
{
    /**
     * The public identifier. Null if unknown.
     */
    final String publicId;

    /**
     * The system identifier.  Null if unknown.
     */
    final String systemId;

    /**
     * The resource name, if a copy of the document is available.
     */
    final String resource;

    /**
     * The namespace URI. Null if unknown.
     */
    final String namespace;

    /**
     * The default namespace prefix.
     */
    final String prefix;

    public DTDInfo( final String publicId, 
                    final String systemId, 
                    final String namespace, 
                    final String prefix, 
                    final String resource )
    {
        this.publicId = publicId;
        this.systemId = systemId;
        this.resource = resource;
        this.namespace = namespace;
        this.prefix = prefix;
    }

}
