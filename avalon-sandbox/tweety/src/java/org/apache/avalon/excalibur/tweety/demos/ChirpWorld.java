/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (c) 2002 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.avalon.excalibur.tweety.demos;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * Does nothing but chirp whenever an avalon lifecycle method is called on it.
 *
 *@author     <a href="mailto:nicolaken@krysalis.org">Nicola Ken Barozzi</a>
 *@author     <a href="mailto:leosimons@apache.org">Leo Simons</a>
 *@version    1.0.1
 */
public class ChirpWorld
    implements LogEnabled, Contextualizable, Serviceable, Initializable, Startable, Disposable
{
    private Logger logger;

    //empty constructor
    public ChirpWorld()
    {
    }

    public void enableLogging( Logger logger )
    {
        this.logger = logger;
        logger.debug( "tweety.demos.ChirpWorld: enableLogging() called, Logger gotten" );
    }

    public void contextualize( Context context )
    {
        logger.debug( "tweety.demos.ChirpWorld: contextualize() called, Context gotten" );
    }

    public void service( ServiceManager cm )
    {
        logger.debug( "tweety.demos.ChirpWorld: service() called, ServiceManager gotten" );
    }

    public void initialize()
    {
        logger.debug( "tweety.demos.ChirpWorld: initialize() called" );
    }

    public void start()
    {
        logger.debug( "tweety.demos.ChirpWorld: start() called" );

        logger.info( "tweety.demos.ChirpWorld: I thawgt I saw a pussycat!" );
    }

    public void stop()
    {
        logger.debug( "tweety.demos.ChirpWorld: stop() called" );
    }

    public void dispose()
    {
        logger.debug( "tweety.demos.ChirpWorld: dispose called" );
    }

}
