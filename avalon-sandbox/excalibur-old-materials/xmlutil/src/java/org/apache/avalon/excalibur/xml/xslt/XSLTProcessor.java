/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.xml.xslt;

import javax.xml.transform.Result;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.XMLFilter;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;

/**
 * This is the interface of the XSLT processor.
 *
 * @author <a href="mailto:ovidiu@cup.hp.com">Ovidiu Predescu</a>
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 * @version CVS $Id: XSLTProcessor.java,v 1.4 2002/05/02 11:29:11 cziegeler Exp $
 * @version 1.0
 * @since   July 11, 2001
 */
public interface XSLTProcessor extends Component
{
    /**
     * The role implemented by an <code>XSLTProcessor</code>.
     */
    String ROLE = XSLTProcessor.class.getName();

    /**
     * Set the TransformerFactory for this instance.
     * The <code>factory</code> is invoked to return a
     * <code>TransformerHandler</code> to perform the transformation.
     *
     * @param classname the name of the class implementing
     * <code>TransformerFactory</code> value. If an error is found
     * or the indicated class doesn't implement the required interface
     * the original factory of the component is maintained.
     */
    void setTransformerFactory(String classname);

    /**
     * <p>Return a <code>TransformerHandler</code> for a given
     * stylesheet <code>Source</code>. This can be used in a pipeline to
     * handle the transformation of a stream of SAX events. See {@link
     * org.apache.cocoon.transformation.TraxTransformer#setConsumer} for
     * an example of how to use this method.
     *
     * <p>The additional <code>filter</code> argument, if it's not
     * <code>null</code>, is inserted in the chain SAX events as an XML
     * filter during the parsing or the source document.
     *
     * <p>This method caches the Source object and performs a reparsing
     * only if this changes.
     *
     * @param stylesheet a <code>Source</code> value
     * @param filter a <code>XMLFilter</code> value
     * @return a <code>TransformerHandler</code> value
     * @exception XSLTProcessorException if an error occurs
     */
    TransformerHandler getTransformerHandler( Source stylesheet, XMLFilter filter )
      throws XSLTProcessorException;

    /**
     * Same as {@link #getTransformerHandler(Source,XMLFilter)}, with
     * <code>filter</code> set to <code>null</code>.
     *
     * @param stylesheet a <code>Source</code> value
     * @return a <code>TransformerHandler</code> value
     * @exception XSLTProcessorException if an error occurs
     */
    TransformerHandler getTransformerHandler( Source stylesheet )
      throws XSLTProcessorException;

    /**
     * Applies an XSLT stylesheet to an XML document. The source and
     * stylesheet documents are specified as <code>Source</code>
     * objects. The result of the transformation is placed in
     * <code>result</code>, which should be properly initialized before
     * invoking this method. Any additional parameters passed in
     * <code>params</code> will become arguments to the stylesheet.
     *
     * @param source a <code>Source</code> value
     * @param stylesheet a <code>Source</code> value
     * @param params a <code>Parameters</code> value
     * @param result a <code>Result</code> value
     * @exception XSLTProcessorException if an error occurs
     */
    void transform( Source source, Source stylesheet, Parameters params, Result result )
      throws XSLTProcessorException;
}
