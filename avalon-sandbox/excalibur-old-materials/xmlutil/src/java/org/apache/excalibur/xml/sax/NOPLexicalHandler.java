/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
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
package org.apache.excalibur.xml.sax;

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * This class provides default implementation of the methods specified
 * by the <code>LexicalHandler</code> interface.
 *
 * @author <a href="mailto:mirceatoma@apache.org">Mircea Toma</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/02/27 09:24:45 $
 */
public class NOPLexicalHandler
    implements LexicalHandler
{
    /**
     * Report the start of DTD declarations, if any.
     *
     * @param name The document type name.
     * @param publicId The declared public identifier for the external DTD
     *                 subset, or null if none was declared.
     * @param systemId The declared system identifier for the external DTD
     *                 subset, or null if none was declared.
     */
    public void startDTD( final String name,
                          final String publicId,
                          final String systemId )
        throws SAXException
    {
    }

    /**
     * Report the end of DTD declarations.
     */
    public void endDTD()
        throws SAXException
    {
    }

    /**
     * Report the beginning of an entity.
     *
     * @param name The name of the entity. If it is a parameter entity, the
     *             name will begin with '%'.
     */
    public void startEntity( final String name )
        throws SAXException
    {
    }

    /**
     * Report the end of an entity.
     *
     * @param name The name of the entity that is ending.
     */
    public void endEntity( final String name )
        throws SAXException
    {
    }

    /**
     * Report the start of a CDATA section.
     */
    public void startCDATA()
        throws SAXException
    {
    }

    /**
     * Report the end of a CDATA section.
     */
    public void endCDATA()
        throws SAXException
    {
    }

    /**
     * Report an XML comment anywhere in the document.
     *
     * @param ch An array holding the characters in the comment.
     * @param start The starting position in the array.
     * @param len The number of characters to use from the array.
     */
    public void comment( final char[] ch,
                         final int start,
                         final int len )
        throws SAXException
    {
    }
}
