/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.tweety.demos;

import org.apache.avalon.framework.activity.*;
import org.apache.avalon.framework.component.*;
import org.apache.avalon.framework.configuration.*;
import org.apache.avalon.framework.context.*;
import org.apache.avalon.framework.logger.*;
import org.apache.avalon.framework.parameters.*;
import org.apache.avalon.framework.service.*;
import org.apache.avalon.framework.container.*;
   
/**
 * Just... chirps :-)
 *
 *@author     <a href="mailto:nicolaken@krysalis.org">Nicola Ken Barozzi</a>
 *@created    June 20, 2002
 *@version    1.0
 */
                
public class ChirpWorld implements LogEnabled, Contextualizable, Composable, Serviceable, Initializable, Startable, Disposable
{
   private Logger  logger;
   private Context context;
   private ComponentManager cm;
   private ServiceManager   sm;   
   
  //empty constructor
  ChirpWorld(){}

  public void enableLogging( Logger logger )
  {
    this.logger = logger;
    logger.debug("enableLogging called, Logger gotten");
  }

  public void contextualize( Context context )
  {
    this.context = context;
    logger.debug("contextualize called, Context gotten");
  }
  
  public void compose( ComponentManager cm )
  {
    this.cm = cm;
    logger.debug("compose called, ComponentManager gotten");
  }   

  public void service( ServiceManager cm )
  {
    this.sm = sm;
    logger.debug("service called, ServiceManager gotten");
  }  
  
  public void initialize()
  {
    logger.debug("initialize called");
  }
  
  public void start()
  {
    logger.debug("start called");
  }

  public void stop()
  {
    logger.debug("stop called");
  }

  public void dispose()
  {
    logger.debug("dispose called");
  }
  
}






