/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

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
package org.apache.avalon.excalibur.logger;

import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.excalibur.logger.logkit.LogKitAdapter;
import org.apache.avalon.excalibur.logger.logkit.LogKitLoggerHelper;
import org.apache.avalon.excalibur.logger.logkit.LogKitConfHelper;
import org.apache.avalon.excalibur.logger.log4j.Log4JConfAdapter;
import org.apache.avalon.excalibur.logger.decorator.LogToSelfDecorator;
import org.apache.avalon.excalibur.logger.decorator.PrefixDecorator;
import org.apache.avalon.excalibur.logger.decorator.CachingDecorator;
import org.apache.avalon.excalibur.logger.util.LoggerManagerTee;
import org.apache.log.Hierarchy;

/**
 * A facade to the modularized *LoggerManager building system.
 * Add methods here to create LoggerManagers to your preference.
 *
 * @author <a href="http://cvs.apache.org/~atagunov">Anton Tagunov</a>
 * @version CVS $Revision: 1.1.1.1 $ $Date: 2003/10/02 19:18:44 $
 * @since 4.0
 */

public class Facade
{
    /**
     * Assemble a new LoggerManager running on top of LogKit
     * configured from a configuration file logging to a supplied
     * logger as a fallback.
     * Use this method as a sample showing how to assemble your
     * own LoggerManager running on top of LogKit flavour.
     */
    public static LoggerManager createLogKitConfigurable( 
            final String prefix, final String switchTo )
    {
        final org.apache.log.Hierarchy hierarchy = new Hierarchy();

        final LoggerManager bare = new LogKitAdapter( hierarchy );
        final LoggerManager decorated = applyDecorators( bare, prefix, switchTo );
        final LoggerManagerTee tee = new LoggerManagerTee( decorated );

        tee.addTee( new LogKitLoggerHelper( hierarchy ) );
        tee.addTee( new LogKitConfHelper( hierarchy ) );
        tee.makeReadOnly();

        return tee;
    }

    /**
     * Assemble LoggerManager for Log4J system configured
     * via a configuration file. All the logging errors
     * will go to System.err however.
     */
    public static LoggerManager createLog4JConfigurable(
            final String prefix, final String switchTo )
    {
        final LoggerManager bare = new Log4JConfAdapter();
        final LoggerManager decorated = applyDecorators( bare, prefix, switchTo );
        return decorated;
    }

    private static LoggerManager applyDecorators( LoggerManager target,
            final String prefix, final String switchTo )
    {
        if ( switchTo != null )
        {
            target = new LogToSelfDecorator( target, switchTo );
        }
        if ( prefix != null && prefix.length() > 0 )
        {
            target = new PrefixDecorator( target, prefix );
        }
        target = new CachingDecorator( target );
        return target;
    }
}
