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
 * Does nothing but chirp whenever an avalon lifecycle method is called on it.
 *
 *@author     <a href="mailto:nicolaken@krysalis.org">Nicola Ken Barozzi</a>
 *@author     <a href="mailto:leosimons@apache.org">Leo Simons</a>
 *@created    June 20, 2002
 *@version    1.0.1
 */

public class ChirpWorld implements LogEnabled, Contextualizable, Composable, Serviceable, Initializable, Startable, Disposable
{
   private Logger  logger;
   private Context context;
   private ComponentManager cm;
   private ServiceManager   sm;

  //empty constructor
  public ChirpWorld(){}

  public void enableLogging( Logger logger )
  {
    this.logger = logger;
    logger.debug("ChirpWorld: enableLogging() called, Logger gotten");
  }

  public void contextualize( Context context )
  {
    this.context = context;
    logger.debug("ChirpWorld: contextualize() called, Context gotten");
  }

  public void compose( ComponentManager cm )
  {
    this.cm = cm;
    logger.debug("ChirpWorld: compose() called, ComponentManager gotten");
  }

  public void service( ServiceManager cm )
  {
    this.sm = sm;
    logger.debug("ChirpWorld: service() called, ServiceManager gotten");
  }

  public void initialize()
  {
    logger.debug("ChirpWorld: initialize() called");
  }

  public void start()
  {
    logger.debug("ChirpWorld: start() called");

	logger.info( "ChirpWorld: I thawgt I saw a pussycat!" );
  }

  public void stop()
  {
    logger.debug("ChirpWorld: stop() called");
  }

  public void dispose()
  {
    logger.debug("ChirpWorld: dispose called");
  }

}
