/*
License $Id: JoAvalonLogListener.java,v 1.1 2002/09/22 09:35:01 hammant Exp $

Copyright (c) 2001-2002 tagtraum industries.

LGPL
====

jo! is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

jo! is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For LGPL see <http://www.fsf.org/copyleft/lesser.txt>


Sun license
===========

This release contains software by Sun Microsystems. Therefore
the following conditions have to be met, too. They apply to the
files

 - lib/mail.jar
 - lib/activation.jar
 - lib/jsse.jar
 - lib/jcert.jar
 - lib/jaxp.jar
 - lib/crimson.jar
 - lib/servlet.jar
 - lib/jnet.jar
 - lib/jaas.jar
 - lib/jaasmod.jar

contained in this release.

a. Licensee may not modify the Java Platform
   Interface (JPI, identified as classes contained within the javax
   package or any subpackages of the javax package), by creating additional
   classes within the JPI or otherwise causing the addition to or modification
   of the classes in the JPI.  In the event that Licensee creates any
   Java-related API and distribute such API to others for applet or
   application development, you must promptly publish broadly, an accurate
   specification for such API for free use by all developers of Java-based
   software.

b. Software is confidential copyrighted information of Sun and
   title to all copies is retained by Sun and/or its licensors.  Licensee
   shall not modify, decompile, disassemble, decrypt, extract, or otherwise
   reverse engineer Software.  Software may not be leased, assigned, or
   sublicensed, in whole or in part.  Software is not designed or intended
   for use in on-line control of aircraft, air traffic, aircraft navigation
   or aircraft communications; or in the design, construction, operation or
   maintenance of any nuclear facility.  Licensee warrants that it will not
   use or redistribute the Software for such purposes.

c. Software is provided "AS IS," without a warranty
   of any kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,
   INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
   PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED.

d. This License is effective until terminated.  Licensee may
   terminate this License at any time by destroying all copies of Software.
   This License will terminate immediately without notice from Sun if Licensee
   fails to comply with any provision of this License.  Upon such termination,
   Licensee must destroy all copies of Software.

e. Software, including technical data, is subject to U.S.
   export control laws, including the U.S.  Export Administration Act and its
   associated regulations, and may be subject to export or import regulations
   in other countries.  Licensee agrees to comply strictly with all such
   regulations and acknowledges that it has the responsibility to obtain
   licenses to export, re-export, or import Software.  Software may not be
   downloaded, or otherwise exported or re-exported (i) into, or to a national
   or resident of, Cuba, Iraq, Iran, North Korea, Libya, Sudan, Syria or any
   country to which the U.S. has embargoed goods; or (ii) to anyone on the
   U.S. Treasury Department's list of Specially Designated Nations or the U.S.
   Commerce Department's Table of Denial Orders.


Feedback
========

We encourage your feedback and suggestions and want to use your feedback to
improve the Software. Send all such feedback to:
<feedback@tagtraum.com>
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.apps.sevak.blocks.jo;

import com.tagtraum.framework.log.C_Log;
import com.tagtraum.framework.log.I_LogEventListener;
import com.tagtraum.framework.log.Log;
import com.tagtraum.framework.log.LogEvent;
import org.apache.avalon.framework.logger.Logger;

/**
 * A connector between the tagtraum log packages and avalon logging.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 * @version $Revision: 1.1 $
 */
public class JoAvalonLogListener implements I_LogEventListener {

    private String name;
    private Logger logger;

    public JoAvalonLogListener(String name, Logger logger) {
        this.name = name;        // source
        this.logger = logger;
        Log.getLog(name).addI_LogEventListener(this);
    }

    public void handleLogEvent(LogEvent le) {
        switch (le.getLevel()) {
            case C_Log.NOLOG:
                break;
            case C_Log.ERROR:
                if (le.getMessage() != null && le.getThrowable() != null) logger.error(le.getMessage(), le.getThrowable());
                else if (le.getMessage() != null) logger.error(le.getMessage());
                else if (le.getThrowable() != null) logger.error(le.getThrowable().toString(), le.getThrowable());
                break;
            case C_Log.MODULE:
                if (le.getMessage() != null && le.getThrowable() != null) logger.info(le.getMessage(), le.getThrowable());
                else if (le.getMessage() != null) logger.info(le.getMessage());
                else if (le.getThrowable() != null) logger.info(le.getThrowable().toString(), le.getThrowable());
                break;
            default:
                if (le.getMessage() != null && le.getThrowable() != null) logger.debug(le.getMessage(), le.getThrowable());
                else if (le.getMessage() != null) logger.debug(le.getMessage());
                else if (le.getThrowable() != null) logger.debug(le.getThrowable().toString(), le.getThrowable());
        }
    }

    public String getName() {
        return name;
    }

    public Logger getLogger() {
        return logger;
    }

    public int hashCode() {
        return name.hashCode() ^ logger.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() == getClass()) {
            return logger.equals(((JoAvalonLogListener)obj).getLogger()) && name.equals(((JoAvalonLogListener)obj).getName());
        }
        return false;
    }

}
