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
package org.apache.excalibur.event;


/**
 * Enqueue predicates allow users to specify a method that
 * will 'screen' elements being enqueued onto a sink, either
 * accepting or rejecting them. This mechanism can be used
 * to implement many interesting load-conditioning policies,
 * for example, simple thresholding, rate control, credit-based
 * flow control, and so forth. Note that the enqueue predicate
 * runs in the context of the <b>caller of enqueue()</b>, which
 * means it must be simple and fast.
 *
 * @version $Revision: 1.5 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface EnqueuePredicate
{
    /**
     * Tests the given element for acceptance onto the m_sink.
     * @since Feb 10, 2003
     *
     * @param  element  The element to enqueue
     * @param  modifyingSing  The sink that is used for this predicate
     * @return
     *  <code>true</code> if the sink accepts the element;
     *  <code>false</code> otherwise.
     */
    boolean accept(Object element, Sink modifyingSink);

    /**
     * Tests the given element for acceptance onto the m_sink.
     * @since Feb 10, 2003
     *
     * @param  elements  The array of elements to enqueue
     * @param  modifyingSing  The sink that is used for this predicate
     * @return
     *  <code>true</code> if the sink accepts all the elements;
     *  <code>false</code> otherwise.
     */
    boolean accept(Object elements[], Sink modifyingSink);
}