/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 2002-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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
package org.apache.avalon.phoenix.interfaces;

/**
 * A set of constants that are used internally in the container to communicate
 * about different artefacts. They usually act as keys into maps.
 *
 * @author Peter Donald
 */
public interface ContainerConstants
{
    /**
     * The name of the software. (Usually phoenix but different
     * users may overide this).
     */
    String SOFTWARE = "@@NAME@@";

    /**
     * The version of the software.
     */
    String VERSION = "@@VERSION@@";

    /**
     * The date on which software was built.
     */
    String DATE = "@@DATE@@";

    /**
     * The name of the attribute used to determine whether
     * a block is not proxied.
     */
    String DISABLE_PROXY_ATTR = "phoenix:disable-proxy";

    /**
     * The name which the assembly is registered into phoenix
     * using.
     */
    String ASSEMBLY_NAME = "phoenix:assembly-name";

    /**
     * The name of the config file which is used
     * to load config data.
     */
    String CONFIG_DESCRIPTOR = "phoenix:config";

    /**
     * The name of the config file which is used
     * to load assembly data.
     */
    String ASSEMBLY_DESCRIPTOR = "phoenix:assembly";

    /**
     * The default classloader to use to load components.
     */
    String ASSEMBLY_CLASSLOADER = "phoenix:classloader";

    /**
     * The name of the partition in which blocks are contained.
     */
    String BLOCK_PARTITION = "block";

    /**
     * The name of the partition in which listeners are contained.
     */
    String LISTENER_PARTITION = "listener";

    /**
     * The root instrumentation category for all applications.
     */
    String ROOT_INSTRUMENT_CATEGORY = "applications";

    /**
     * The source of installation (usually a directory in .sar format or a .sar file).
     * Type: {@link java.io.File}
     */
    String INSTALL_SOURCE = "install:source";

    /**
     * The Directory in which application is installed.
     * Type: {@link java.io.File}
     */
    String INSTALL_HOME = "install:home";

    /**
     * The Directory in which application temporary/work data is stored.
     * Type: {@link java.io.File}
     */
    String INSTALL_WORK = "install:work";

    /**
     * The URL to block configuration data.
     * Type: {@link java.lang.String}
     */
    String INSTALL_CONFIG = "install:config";

    /**
     * The URL to assembly data.
     * Type: {@link java.lang.String}
     */
    String INSTALL_ASSEMBLY = "install:assembly";

    /**
     * The URL to application configuration data.
     * Type: {@link java.lang.String}
     */
    String INSTALL_ENVIRONMENT = "install:environment";
}
