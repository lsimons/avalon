/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.avalon.framework.logger;

/**
 * The Null Logger class.  This is useful for implementations where you need
 * to provide a logger to a utility class, but do not want any output from it.
 * It also helps when you have a utility that does not have a logger to supply.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.11 $ $Date: 2003/02/11 15:58:41 $
 */
public final class NullLogger implements Logger
{
    /**
     * Creates a new <code>NullLogger</code>.
     */
    public NullLogger()
    {
    }

    /**
     * No-op.
     *
     * @param message ignored
     */
    public void debug( String message )
    {
    }

    /**
     * No-op.
     *
     * @param message ignored
     * @param throwable ignored
     */
    public void debug( String message, Throwable throwable )
    {
    }

    /**
     * No-op.
     *
     * @return <code>false</code>
     */
    public boolean isDebugEnabled()
    {
        return false;
    }

    /**
     * No-op.
     *
     * @param message ignored
     */
    public void info( String message )
    {
    }

    /**
     * No-op.
     *
     * @param message ignored
     * @param throwable ignored
     */
    public void info( String message, Throwable throwable )
    {
    }

    /**
     * No-op.
     *
     * @return <code>false</code>
     */
    public boolean isInfoEnabled()
    {
        return false;
    }

    /**
     * No-op.
     *
     * @param message ignored
     */
    public void warn( String message )
    {
    }

    /**
     * No-op.
     *
     * @param message ignored
     * @param throwable ignored
     */
    public void warn( String message, Throwable throwable )
    {
    }

    /**
     * No-op.
     *
     * @return <code>false</code>
     */
    public boolean isWarnEnabled()
    {
        return false;
    }

    /**
     * No-op.
     *
     * @param message ignored
     */
    public void error( String message )
    {
    }

    /**
     * No-op.
     *
     * @param message ignored
     * @param throwable ignored
     */
    public void error( String message, Throwable throwable )
    {
    }

    /**
     * No-op.
     *
     * @return <code>false</code>
     */
    public boolean isErrorEnabled()
    {
        return false;
    }

    /**
     * No-op.
     *
     * @param message ignored
     */
    public void fatalError( String message )
    {
    }

    /**
     * No-op.
     *
     * @param message ignored
     * @param throwable ignored
     */
    public void fatalError( String message, Throwable throwable )
    {
    }

    /**
     * No-op.
     *
     * @return <code>false</code>
     */
    public boolean isFatalErrorEnabled()
    {
        return false;
    }

    /**
     * Returns this <code>NullLogger</code>.
     *
     * @param name ignored
     * @return this <code>NullLogger</code>
     */
    public Logger getChildLogger( String name )
    {
        return this;
    }
}
