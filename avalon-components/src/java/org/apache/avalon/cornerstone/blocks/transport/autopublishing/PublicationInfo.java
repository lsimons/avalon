
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.blocks.transport.autopublishing;



public class PublicationInfo
{
   private final String mPublishAsName;
   private final String mInterfaceToPublish;

   public PublicationInfo (String publishAsName, String interfaceToPublish)
   {
      mPublishAsName      = publishAsName;
      mInterfaceToPublish = interfaceToPublish;
   }

   public String getPublishAsName ()
   {
      return mPublishAsName;
   }

   public String getInterfaceToPublish ()
   {
      return mInterfaceToPublish;
   }
}


/*------ Formatted by Jindent 3.24 Basic 1.0 --- http://www.jindent.de ------*/
