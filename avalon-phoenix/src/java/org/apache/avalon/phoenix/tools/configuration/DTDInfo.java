/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.configuration;

/**
 * Holds information about a given DTD.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2002/02/26 11:13:04 $
 */
public class DTDInfo
{
   /**
    * The public identifier. Null if unknown.
    */
   private final String m_publicId;

   /**
    * The system identifier.  Null if unknown.
    */
   private final String m_systemId;

   /**
    * The resource name, if a copy of the document is available.
    */
   private final String m_resource;

   public DTDInfo( final String publicId,
                   final String systemId,
                   final String resource )
   {
      m_publicId = publicId;
      m_systemId = systemId;
      m_resource = resource;
   }

   public String getPublicId()
   {
      return m_publicId;
   }

   public String getSystemId()
   {
      return m_systemId;
   }

   public String getResource()
   {
      return m_resource;
   }
}
