/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.logging.logkit;

import java.io.File;

import org.apache.avalon.framework.logger.Logger;

import org.apache.avalon.logging.provider.LoggingException;

import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.provider.Factory;

/**
 * A LogTargetFactoryBuilder provides support for the establishment of 
 * new logging targets.
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/02/24 21:55:39 $
 */
public interface LogTargetFactoryBuilder
{
   /**
    * Build a log target factory using a supplied class.  The implementation
    * checks the first available constructor arguments and builds a set of 
    * arguments based on the arguments supplied to this builder instance.
    *
    * @param clazz the log target factory class
    * @return a instance of the class
    * @exception LoggingException if the class does not expose a public 
    *    constructor, or the constructor requires arguments that the 
    *    builder cannot resolve, or if a unexpected instantiation error 
    *    ooccurs
    */ 
    public LogTargetFactory buildLogTargetFactory( Class clazz ) 
      throws LoggingException;
}
