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
package org.apache.avalon.excalibur.pool;

/**
 * <code>Poolable</code> is a marker interface for Components that can
 * be pooled.  Components that are not pooled are created anew via a
 * factory every time a request is made for the component.
 * <p>
 * Components implementing this interface can add the following
 * attributes to its definition:
 * <pre><code>
 *   &lt;component pool-min="1" pool-max="10" pool-grow="1"&gt;
 *     &lt;tag&gt;value&lt;/tag&gt;
 *   &lt;/component&gt;
 * </pre></code>
 * Where:
 * <table border="0" cellpadding="4" cellspacing="0">
 *   <tr>
 *     <td valign="top"><code>pool-min</code></td>
 *     <td valign="top">sets the minimum number of Components maintained by the
 *     pool</td>
 *   </tr>
 *   <tr>
 *     <td valign="top"><code>pool-max</code></td>
 *     <td valign="top">sets the maximum number of Components maintained by the
 *     pool</td>
 *   </tr>
 *   <tr>
 *     <td valign="top"><code>pool-grow</code></td>
 *     <td valign="top">sets the number of Components to grow or
 *     shrink the pool by whenever it becomes necessary to do so</td>
 *   </tr>
 * </table>
 * </p><p>
 * NB: It was a deliberate choice not to extend Component. This will have to
 * be reassed once we see it in action.
 * </p>
 *
 * @author Peter Donald
 * @version CVS $Revision: 1.3 $ $Date: 2003/12/05 15:15:15 $
 * @since 4.0
 */
public interface Poolable
{
}
