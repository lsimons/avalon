
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.transport.subscription;



import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.commons.altrmi.client.impl.rmi.RmiAltrmiHostContext;


/**
 * Class RmiSubscriber
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.2 $
 */

public class RmiSubscriber
   extends AbstractSubscriber
{
   private String mHost;
   private int    mPort;

   /**
    * Pass the <code>Configuration</code> to the <code>Configurable</code>
    * class. This method must always be called after the constructor
    * and before any other method.
    *
    * @param configuration the class configurations.
    */

   public void configure (Configuration configuration)
      throws ConfigurationException
   {
      super.configure(configuration);

      mPort = configuration.getChild("port").getValueAsInteger();
      mHost = configuration.getChild("host").getValue();
   }

   /**
    * Initialialize the component. Initialization includes
    * allocating any resources required throughout the
    * components lifecycle.
    *
    * @exception Exception if an error occurs
    */

   public void initialize ()
      throws Exception
   {
      mHostContext = new RmiAltrmiHostContext(mHost, mPort);

      super.initialize();
   }
}


/*------ Formatted by Jindent 3.24 Basic 1.0 --- http://www.jindent.de ------*/
