/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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
package org.apache.avalon.excalibur.logger.factory;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.avalon.excalibur.logger.LogTargetFactory;

import org.apache.log.LogTarget;
import org.apache.log.format.PatternFormatter;
import org.apache.log.output.lf5.LF5LogTarget;

/**
 * A factory for the <a href="http://jakarta.apache.org/log4j/docs/lf5/features.html">LogFactor5</a>
 * Swing GUI.
 * <p>
 * Configuration :
 * <pre>
 * &lt;lf5 id="target-id"&gt;
 *   &lt;NDC-format type="raw|pattern|extended"&gt;pattern to be used&lt;/NDC-format&gt;
 * &lt;/lf5&gt;
 * </pre>
 *
 * The optional "NDC-pattern" configuration defines the pattern that will be used to 
 * format the log event for display on the "NDC" line in the Swing GUI.
 *
 * @author <a href="sylvain@apache.org">Sylvain Wallez</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/01/23 09:10:14 $
 */

public class LF5TargetFactory implements LogTargetFactory
{
    public LogTarget createTarget( final Configuration configuration )
      throws ConfigurationException
    {
        LF5LogTarget result = new LF5LogTarget();
        
        Configuration child = configuration.getChild( "NDC-pattern", false );
        if ( null != child )
        {
            result.setNDCFormatter( new FormatterFactory().createFormatter( child ) );
        }
        
        return result;
    }
}
