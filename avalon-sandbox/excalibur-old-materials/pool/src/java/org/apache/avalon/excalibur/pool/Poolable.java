/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
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
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 05:09:04 $
 * @since 4.0
 */
public interface Poolable
{
}
