
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.blocks.transport.autopublishing;

import org.apache.commons.altrmi.server.AltrmiPublisher;
import org.apache.commons.altrmi.server.AltrmiPublicationException;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.phoenix.BlockEvent;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import java.util.Vector;

/**
 * Class AutoPublisher
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.1 $
 */
public class AutoPublisher
   implements BlockListener, Configurable
{
   private AltrmiPublisher mAltrmiPublisher;
   private Vector          mServicesToPublish = new Vector();
   private Vector          mPublishAsNames    = new Vector();

   /**
    *
    *
    *      IN PROGRESS & UNFINISHED - PAUL H
    *
    *
    */


   /**
    * Pass the <code>Configuration</code> to the <code>Configurable</code>
    * class. This method must always be called after the constructor
    * and before any other method.
    *
    * @param configuration the class configurations.
    */
   public void configure (final Configuration configuration)
      throws ConfigurationException
   {
      System.out.println("AutoPublisher.configure() called!");

      Configuration[] publications =
         configuration.getChildren("publications");

      for (int i = 0; i < publications.length; i++)
      {
         Configuration publication = publications [i];

         System.out.println("publications " + publication);
      }
   }

   /**
    * Notification that a block has just been added
    * to Server Application.
    *
    * @param event the BlockEvent
    */

   public void blockAdded (final BlockEvent event)
   {
      try
      {
         if (mServicesToPublish.contains(event.getName()))
         {
            int                 ix         =
               mServicesToPublish.indexOf(event.getName());
            Block               block      = event.getBlock();
            BlockInfo           bi         = event.getBlockInfo();
            ServiceDescriptor[] sd         = bi.getServices();
            Vector              classNames = new Vector();

            for (int i = 0; i < sd.length; i++)
            {
               ServiceDescriptor descriptor = sd [i];
               Class             clazz      =
                  Class.forName(descriptor.getName());

               classNames.add(clazz);
            }

            Class[] classes = new Class [classNames.size()];

            classNames.copyInto(classes);
            //mAltrmiPublisher.publish(block,
            //                         ( String ) mPublishAsNames.elementAt(ix),
            //                         classes);
         }
      }
      catch (ClassNotFoundException cnfe)
      {
         cnfe.printStackTrace();

         // TODO ?
      }
      catch (AltrmiPublicationException ape)
      {
         ape.printStackTrace();

         // TODO ?
      }
   }

   /**
    * Notification that a block is just about to be
    * removed from Server Application.
    *
    * @param event the BlockEvent
    */

   public void blockRemoved (final BlockEvent event)
   {
      System.out.println("Removed block '" + event.getName() + "'");
   }
}
